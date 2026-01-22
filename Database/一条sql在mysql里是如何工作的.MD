一条 SQL 在 MySQL 中的“旅程”可以拆成两大阶段：  
1. Server 层（连接、权限、解析、优化、执行）  
2. 存储引擎层（InnoDB、MyISAM 等，真正读写磁盘）

下面用一条最简单的 `UPDATE t SET c = c + 1 WHERE id = 10;` 带你走一遍完整流程，并给出每一步在源码/日志中的落点，方便你实际跟踪。

--------------------------------------------------
一、连接与线程
1. 客户端通过 TCP/Unix Socket 连到 3306 端口。  
2. MySQL 为每个连接分配一条**系统线程**（`sql/sql_connect.cc` 中的 `create_new_thread`）。  
3. 线程先检查 `mysql.user` 表做身份认证，再把连接放进线程池（`thread_pool` 插件）或保持单线程模式。  
4. 连接成功后，线程会分配一个 `THD` 对象（Thread Handler），它贯穿整条 SQL 的生命周期，保存了用户变量、临时表、诊断信息等。

--------------------------------------------------
二、解析与预处理（Server 层）
1. 接收网络包 → `my_net_read()` → 把原始文本放进 `THD::query_string`。  
2. **词法分析** (`sql/sql_lex.cc`, `lex_one_token`)：拆成 token（UPDATE / t / SET …）。  
3. **语法分析** (`sql/sql_yacc.yy`, bison 生成)：生成**解析树** `Parse_tree_root`。  
4. **预处理器** (`sql/sql_resolver.cc`)：  
   - 检查表、列是否存在；  
   - 把 `*` 展开成列列表；  
   - 把视图替换成基表；  
   - 权限预检（`check_access()`）。  
   结果得到一条“语句对象” `sql_cmd`（本例是 `Sql_cmd_update`）。

--------------------------------------------------
三、查询优化器（Server 层）
1. 打开表 (`open_and_lock_tables`) → 加载 InnoDB 元数据，加 MDL（Metadata Lock）。  
2. 优化器入口：`handle_query()` → `JOIN::optimize()`  
   - 规则优化：常量折叠、表达式简化、子查询转换（`IN→EXISTS` / `derived table`）。  
   - 代价优化：  
     – 选择索引（`range/index_merge` 分析，`sql/opt_range.cc`）；  
     – 估算行数（`records_per_key` 来自 InnoDB 的统计表）；  
     – 生成执行计划（`AccessPath`，8.0 的新框架）。  
   本例 WHERE 走主键 `id=10`，优化器决定用 `ref access`。

--------------------------------------------------
四、执行器（Server 层）
1. 根据执行计划调用 `Sql_cmd_update::execute()` → `mysql_update()`。  
2. 执行器先请求 InnoDB：“给我 id=10 的行”  
   – `ha_innobase::index_read()` → 把主键 10 传给 InnoDB 层。  
3. InnoDB 返回行后，执行器把 c 字段加 1，再调用  
   – `ha_innobase::update_row()` 把新行写回去。  
4. 每改一行，先写**回滚段 undo**、再写**聚集索引**、再写**二级索引**，同时把**修改前的镜像**记入 undo log，用于回滚与 MVCC。  
5. 如果表上有触发器、外键、级联，执行器会递归调用相应逻辑。

--------------------------------------------------
五、存储引擎层（InnoDB）
1. Buffer Pool 查找  
   - 主键 10 所在的 16 KB 页在 BP 命中则直接返回；  
   - 未命中则从 `.ibd` 文件读页，异步预读 (`read_ahead`)。  
2. 加锁  
   - 先给聚簇索引记录加**行锁** (`lock_rec_lock`)，类型为 `LOCK_X | LOCK_REC_NOT_GAP`；  
   - 如果隔离级别是 RR 且走范围，还会加 **gap lock / next-key lock**。  
3. 写 redo log（WAL）  
   - 修改页前先写 redo log buffer (`mlog_write_ulint`…)；  
   - `trx_flush_log_later` 决定是否立即刷盘；  
   - commit 时 `fsync()` 保证持久化（参数 `innodb_flush_log_at_trx_commit`）。  
4. 写 undo log  
   - 在系统回滚段表空间 (`ibdata1` 或独立 undo tablespace) 插入一条 undo record；  
   - undo 记录包含旧值和指向上一版本指针，用于一致性读与回滚。  
5. 修改页  
   - 把 BP 页设为 dirty，加入 `flush_list`，由后台线程 `page_cleaner` 异步刷盘。  
6. Change Buffer（二级索引延迟写）  
   - 若二级索引页不在 BP，先写 insert buffer，后续读/合并时再真正更新。

--------------------------------------------------
六、二阶段提交（binlog & redo 一致性）
1. 执行器在真正 commit 前，先调用 `ha_prepare_low()` 让 InnoDB 进入 prepare 状态：  
   – 把事务 XID 写到 redo log，并置 `TRX_PREPARED` 标志。  
2. Server 层把本次产生的 binlog 写入 binlog cache，并做一次 `fsync()`（`sync_binlog=1`）。  
3. 再调用 `ha_commit_low()` 让 InnoDB 把事务状态从 `TRX_PREPARED` → `TRX_COMMITTED_IN_MEMORY`，释放行锁、清理 undo。  
4. 若在第 2 步之后崩溃，重启时先扫 redo log 找到 prepared 事务，再扫 binlog：  
   – 如果 binlog 也存在该 XID → 提交；  
   – 如果 binlog 没有 → 回滚。  
   从而保证主从一致。

--------------------------------------------------
七、返回客户端
1. Server 层把受影响行数、warning 计数、last-insert-id 等写进协议包 → `my_net_write()`。  
2. 线程把 `THD` 放回连接池（线程池模式）或继续等待下一条命令。  
3. 如果开启了 `general_log`，整条 SQL 文本和耗时会被记入 `mysql.general_log` 表；  
   如果 `slow_query_log` 打开且超过阈值，会写慢日志，并可通过 `mysqldumpslow` 分析。

--------------------------------------------------
八、常用跟踪姿势
1. 看执行计划：`EXPLAIN FORMAT=JSON UPDATE t SET c=c+1 WHERE id=10;`  
2. 看加锁：`SHOW ENGINE INNODB STATUS\G` —— 输出 `TRANSACTIONS` 段。  
3. 看 redo/undo 量：`SHOW GLOBAL STATUS LIKE 'Innodb_log_writes%';`  
4. 看是否用到 change buffer：`SHOW STATUS LIKE 'Innodb_ibuf%';`  
5. 跟踪源码断点：  
   – `Sql_cmd_update::execute()` → `mysql_update()` → `ha_innobase::update_row()`  
   – `lock_rec_lock()` 看行锁；  
   – `log_write_up_to()` 看 redo 刷盘。

--------------------------------------------------
一句话总结  
Server 层负责“把 SQL 变成执行计划 + 协调各模块”，InnoDB 层负责“把行锁、redo、undo、buffer pool、索引”全部落地；二者通过二阶段提交让 binlog 与 redo log 永远同生共死，从而保证崩溃恢复和主从一致。
