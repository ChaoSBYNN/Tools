# CDC (变化数据捕获)

> 在进行数据ETL过程中，我们经常需要通过周期性的定时调度将业务数据按照T+1的方式同步到数据仓库中，进行数据分析处理，最终通过BI报表展示给最终用户，但这种方式实时性较差，用户往往只能看到昨天的数据，会影响用户决策的及时性；而如果用户要近实时的查看报表，则需要将调度周期频率提高到小时或分钟，这对整个数据分析系统是个很大的考验；而上述过程也只适用于数据不断新增的情况，如果遇到业务数据修改、删除的过程，则只能每次全量同步覆盖；在面对上述数据同步过程中数据时效性要求高、历史数据会变更的问题，我们可以使用变化数据捕获技术进行数据的实时同步。

## 概念

> (增量数据抽取处理) 变化数据捕获 (Change Data Capture，缩写CDC) 是指识别和捕获对数据库中的数据所做的更改（包括数据或数据表的插入、更新、删除等），然后将这些更改按发生的顺序完整记录下来，并实时通过消息中间件传送到下游流程或系统的过程。通过这种方式，CDC能够向数据仓库提供高效、低延迟的数据传输，以便信息被及时转换并交付给专供分析的应用程序。

### 优点

对于各种时间敏感类的数据很适合通过CDC的方式进行同步传输，其具备以下好处：

- 通过增量加载或将数据更改实时流式传输，而无需周期性调度执行批量加载更新操作。
- CDC实时同步传输数据，它利于不停机的数据库迁移，并支持实时分析，可以帮助用户根据最新的数据做出更快、更准确的决策。
- CDC最大限度地减少了数据的传输网络流量，适合跨广域网传输数据。
- CDC可以确保多个系统中的数据保持同步。

- 实时性：能够几乎实时地将数据变更同步到目标系统中，保持数据的实时性。
- 高效性：只传输变更数据，减少了数据传输量，降低了网络带宽和存储需求。
- 灵活性：能够支持不同类型的数据源和目标系统，并适应复杂的数据同步需求。
- 可靠性：通过对变更操作进行校验和持久化，保证数据同步的可靠性和一致性。

### 场景

CDC技术的应用场景非常广泛，包括：

- 数据分发：将一个数据源的数据分发给多个下游业务系统，常用于业务解耦、微服务系统。
- 数据采集：面向数据仓库、数据湖的ETL数据集成，消除数据孤岛，便于后续的分析。
- 数据同步：常用于数据备份、容灾等。

## 分类

|分类|类型|描述|
|:----:|:----:|:----:|
|模式|||
||入侵式|CDC操作会给源系统带来影响, 源库执行SQL|
||非入侵式|对源系统不具入侵性|
|方法|||
||时间戳|需要源系统有相应的数据列表示最后的数据变化|
||快照|以使用数据库系统自带的机制实现，如Oracle的物化视图技术，也可以自己实现相关逻辑，但会比较复杂|
||触发器|是关系数据库系统具有的特性，源表上建立的触发器会在对该表执行insert,update,delete等语句时被触发，触发器中的逻辑用于捕获数据的变化|
||日志|可以使用应用日志或系统日志，这种方式对源系统不具有侵入性，但需要额外的日志解析工作|

> 总结
> - 基于时间戳的CDC、基于触发器的CDC、基于快照的CDC是侵入性的；
> - 基于日志的CDC是非侵入性的。

|时间戳方式|快照方式|触发器方式|日志方式|
|:----:|:----:|:----:|:----:|
|能区分插入/更新|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|周期内，检测到多次更新|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|
|能检测到删除|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|不具有侵入性|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_check_mark:|
|支持实时|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|
|需要DBA|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|
|不依赖数据库|:heavy_check_mark:|:heavy_check_mark:|:heavy_multiplication_x:|:heavy_multiplication_x:|

## 厂家方案

- Sql Server CDC
- Oracle CDC
- Flink SQL CDC
- DataFlow CDC
- ETCCloud
- DataX
- Sqoop


- [什么是变化数据捕获(CDC)](https://zhuanlan.zhihu.com/p/541456889)
