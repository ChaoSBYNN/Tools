# MySQL SQL_MODE 详解

`sql_mode` 是 MySQL 中一个至关重要的系统变量，它定义了 MySQL 对 SQL 语法的解析规则、数据有效性的校验标准，以及与其他数据库的兼容策略 。简单来说，它决定了 MySQL 是运行在"宽松模式"还是"严格模式"下。

---

## 一、SQL_MODE 的作用

SQL_MODE 主要影响以下几个方面 ：

| 作用领域 | 说明 |
|---------|------|
| **数据有效性校验** | 阻止无效数据插入/更新（如非法日期、除以零、字符串截断等） |
| **SQL 语法规范** | 限制或支持特定的 SQL 语法（如 GROUP BY 规则、引号使用） |
| **数据库兼容性** | 模拟其他数据库的行为（如 Oracle 的 `||` 连接符） |
| **避免歧义行为** | 明确 SQL 执行逻辑，减少不确定结果 |

---

## 二、如何查看和设置

### 查看当前 SQL_MODE

```sql
-- 查看会话级（当前连接）
SELECT @@SESSION.sql_mode;
-- 或简写
SELECT @@sql_mode;

-- 查看全局级
SELECT @@GLOBAL.sql_mode;
```

MySQL 8.0 默认值为 ：
```
ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
```

### 设置 SQL_MODE

```sql
-- 会话级（仅当前连接有效，断开后失效）
SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';

-- 全局级（对新连接生效，需重启才能完全生效）
SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';
```

### 永久配置

在 `my.cnf` 或 `my.ini` 的 `[mysqld]` 段添加 ：
```ini
[mysqld]
sql_mode=ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
```

---

## 三、核心参数详解

### 1. 严格模式（数据校验）

| 参数 | 作用 | 适用场景 |
|-----|------|---------|
| **`STRICT_TRANS_TABLES`** | 对事务表启用严格模式，拒绝非法数据；对非事务表，首行错误中止，后续行调整并警告  | **生产环境标配** |
| **`STRICT_ALL_TABLES`** | 对所有存储引擎启用严格模式，非事务表遇到错误可能导致部分更新  | 需全面严格校验 |

**严格模式 vs 非严格模式对比** ：

```sql
-- 非严格模式：插入 'abc' 到 INT 列，静默转为 0
SET sql_mode = '';
INSERT INTO test (val) VALUES ('abc');  -- Query OK, 1 warning, 存储为 0

-- 严格模式：直接报错
SET sql_mode = 'STRICT_TRANS_TABLES';
INSERT INTO test (val) VALUES ('abc');  -- ERROR 1366: Incorrect integer value
```

### 2. GROUP BY 规范

| 参数 | 作用 |
|-----|------|
| **`ONLY_FULL_GROUP_BY`** | 强制 SELECT 列表中的所有非聚合列必须出现在 GROUP BY 子句中  |

**示例** ：
```sql
-- 开启 ONLY_FULL_GROUP_BY 时会报错
SELECT * FROM shop GROUP BY article;
-- ERROR: Expression #2 of SELECT list is not in GROUP BY clause...

-- 正确写法
SELECT article, dealer, price FROM shop GROUP BY article, dealer, price;
-- 或使用聚合函数
SELECT article, MAX(price) FROM shop GROUP BY article;
```

### 3. 日期与零值处理

| 参数 | 作用 |
|-----|------|
| **`NO_ZERO_IN_DATE`** | 禁止日期中出现零值（如 `2020-01-00`），需配合严格模式生效  |
| **`NO_ZERO_DATE`** | 禁止插入 `0000-00-00` 日期  |
| **`ERROR_FOR_DIVISION_BY_ZERO`** | 除零操作抛出错误（否则返回 NULL），需配合严格模式  |
| **`ALLOW_INVALID_DATES`** | 允许存储无效日期（如 `2004-04-31`），仅跳过格式校验  |

### 4. ANSI 标准兼容性

| 参数 | 作用 |
|-----|------|
| **`ANSI_QUOTES`** | 双引号 `"` 视为标识符引用（如反引号），字符串必须用单引号  |
| **`PIPES_AS_CONCAT`** | 将 `||` 视为字符串连接符（而非逻辑 OR），兼容 Oracle/PostgreSQL  |
| **`ANSI`** | 组合模式，启用 ANSI SQL 兼容行为  |

### 5. 其他重要参数

| 参数 | 作用 | 备注 |
|-----|------|------|
| **`NO_ENGINE_SUBSTITUTION`** | 指定存储引擎不可用时抛出错误，而非替换为默认引擎  | 生产环境建议开启 |
| **`NO_AUTO_CREATE_USER`** | 禁止 GRANT 语句自动创建用户 | **MySQL 8.0 已移除**  |
| **`IGNORE_SPACE`** | 允许函数名与括号间有空格（如 `COUNT (1)`） | 可能影响内置函数解析 |

---

## 四、预定义组合模式

MySQL 提供了一些预定义的组合模式，方便快速配置 ：

| 组合模式 | 包含的参数 | 用途 |
|---------|-----------|------|
| **`ANSI`** | REAL_AS_FLOAT, PIPES_AS_CONCAT, ANSI_QUOTES, IGNORE_SPACE, ONLY_FULL_GROUP_BY | 标准 SQL 兼容 |
| **`TRADITIONAL`** | STRICT_TRANS_TABLES, STRICT_ALL_TABLES, NO_ZERO_IN_DATE, NO_ZERO_DATE, ERROR_FOR_DIVISION_BY_ZERO, NO_ENGINE_SUBSTITUTION | 传统严格行为 |
| **`ORACLE`** | PIPES_AS_CONCAT, ANSI_QUOTES, IGNORE_SPACE, NO_KEY_OPTIONS... | 兼容 Oracle |
| **`POSTGRESQL`** | PIPES_AS_CONCAT, ANSI_QUOTES, IGNORE_SPACE... | 兼容 PostgreSQL |

---

## 五、生产环境建议配置

### 推荐配置 
```ini
sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,ONLY_FULL_GROUP_BY,NO_ENGINE_SUBSTITUTION'
```

### 配置建议

1. **始终启用严格模式**：避免脏数据入库，保护数据完整性 
2. **保留 ONLY_FULL_GROUP_BY**：确保 GROUP BY 查询结果确定性 
3. **启用 NO_ENGINE_SUBSTITUTION**：确保表使用指定存储引擎（如强制 InnoDB）
4. **谨慎关闭参数**：旧系统迁移时可临时关闭部分校验，但需评估风险

---

## 六、版本差异注意事项

| 版本 | 默认 SQL_MODE | 重要变化 |
|-----|--------------|---------|
| MySQL 5.6 | `NO_ENGINE_SUBSTITUTION`（空值） | 宽松模式 |
| MySQL 5.7 | 包含 `ONLY_FULL_GROUP_BY`、`STRICT_TRANS_TABLES` 等 | 默认启用严格模式  |
| MySQL 8.0 | 同上，但移除 `NO_AUTO_CREATE_USER` | 用户必须显式创建  |

---

## 七、常见问题

### 1. 升级 MySQL 5.6 → 5.7/8.0 后 GROUP BY 报错
**原因**：5.7 默认启用 `ONLY_FULL_GROUP_BY`  
**解决**：修改 SQL 确保非聚合列都在 GROUP BY 中，或临时关闭该模式（不推荐）

### 2. 插入日期 `0000-00-00` 报错
**原因**：默认启用 `NO_ZERO_DATE`  
**解决**：使用合法日期，或修改 `sql_mode` 移除该参数

### 3. STRICT_TRANS_TABLES vs STRICT_ALL_TABLES 选择
- 使用 **InnoDB**（事务表）：两者效果相同，推荐 `STRICT_TRANS_TABLES` 
- 使用 **MyISAM**（非事务表）：`STRICT_ALL_TABLES` 可能导致部分更新问题，建议用 `STRICT_TRANS_TABLES` 
