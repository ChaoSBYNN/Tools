# CDC (变化数据捕获)

## 概念

> 增量数据抽取处理

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
>> - 基于时间戳的CDC、基于触发器的CDC、基于快照的CDC是侵入性的；
>> - 基于日志的CDC是非侵入性的。

|时间戳方式|快照方式|触发器方式|日志方式|
|:----:|:----:|:----:|:----:|
|能区分插入/更新|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|周期内，检测到多次更新|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|
|能检测到删除|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|:heavy_check_mark:|
|不具有侵入性|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_check_mark:|
|支持实时|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|
|需要DBA|:heavy_multiplication_x:|:heavy_multiplication_x:|:heavy_check_mark:|:heavy_check_mark:|
|不依赖数据库|:heavy_check_mark:|:heavy_check_mark:|:heavy_multiplication_x:|:heavy_multiplication_x:|
