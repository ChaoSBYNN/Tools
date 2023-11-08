# Saga

[microservices Saga](https://microservices.io/patterns/data/saga.html)

最强大的事务类型之一称为两阶段提交，当第一个事务的提交取决于第二个事务的完成时，它是摘要。特别是当您必须同时更新多个实体时，例如确认订单和立即更新库存时，它非常有用。

> `commit`, `rollback`

如果微服务使用独享数据库，那么通过分布式事务管理一致性是一个巨大的挑战。你无法使用传统的两阶段提交协议，因为它要么不可伸缩（关系数据库），要么不被支持（多数非关系数据库）。

> 由于一个应用是由一组相互协作的微服务所组成，在分布式环境下由于各个服务访问的数据是相互分离的， 服务之间不能靠数据库来保证事务一致性。 这就需要在应用层面提供一个协调机制，来保证一组事务执行要么成功，要么失败。

![Two Stage](https://pic1.zhimg.com/80/v2-414fa92a6d43a1587499f7ece2e7f0e4_720w.webp)

两阶段提交其实比较简单，这边有两个资源提供准备和提交两个接口。

由于隔离性互斥的要求，在事务执行过程中，所有的资源都是被锁定的，这种情况只适合执行时间确定的短事务。 但是为了保证分布式事务的一致性，大都是采用串行化的隔离级别来保证事务一致性，这样会降低系统的吞吐。

但因为2PC的协议成本比较高，又有全局锁的问题，性能会比较差。 现在大家基本上不会采用这种强一致解决方案。

![ACID & BASE](https://pic2.zhimg.com/80/v2-3acd874629ea5fdb7edb228a8f4a2071_720w.webp)

但您还是可以在微服务架构中使用 Saga 模式实现分布式事务。Saga 是 1987 年开发的一种古老模式，是关系数据库中关于大事务的一个替代概念。但这种模式的一种现代变种对分布式事务也非常有效。Saga 模式是一个本地事务序列，其每个事务在一个单独的微服务内更新数据存储并发布一个事件或消息。Saga 中的首个事务是由外部请求（事件或动作）初始化的，一旦本地事务完成（数据已保存在数据存储且消息或事件已发布），那么发布的消息或事件则会触发 Saga 中的下一个本地事务。


![What](https://pic4.zhimg.com/80/v2-169b9d54539be654df03ea780e2d4dfb_720w.webp)

![Flow](https://pic3.zhimg.com/v2-b74d718af4e44a6c541176426037bc7a_b.jpg)

![Saga](https://pic1.zhimg.com/80/v2-9d7d633aeb10cd53d5ecbe2d980c1f35_720w.webp?source=1940ef5c)

如果本地事务失败，Saga 将执行一系列补偿事务来回滚前面本地事务的更改。

![Where](https://pic1.zhimg.com/80/v2-094bbeb57bab1f761a038d9c4fa627f4_720w.webp)

Saga 事务协调管理主要有两种形式：

- 事件编排 Choreography：分散协调，每个微服务生产并监听其他微服务的事件或消息然后决定是否执行某个动作。
- 命令编排 Orchestration：集中协调，由一个协调器告诉参与的微服务哪个本地事务需要执行。

优点

- 为高可伸缩或松耦合的、事件驱动的微服务架构提供一致性事务。
- 为使用了不支持 2PC 的非关系数据库的微服务架构提供一致性事务。

缺点

- 需要处理瞬时故障，并且提供等幂性。
- 难以调试，而且复杂性随着微服务数量增加而增加。

何时使用 Saga

- 在使用了事件源的高可伸缩、松耦合的微服务中。
- 在使用了分布式非关系数据库的系统中。

何时不宜使用 Saga

- 使用关系数据库的低可伸缩性事务型系统。
- 在服务间存在循环依赖的系统中。

可用技术示例

Axon， Eventuate， Narayana

延伸阅读

- Saga分布式事务-Azure设计模式https://docs.microsoft.com/en-us/azure/architecture/reference-architectures/saga/saga
- 微服务模式：Sagashttps://microservices.io/patterns/data/saga.html
- Saga 模式：微服务中的应用程序事务https://blog.couchbase.com/saga
