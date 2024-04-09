# 命令和查询职责分离（CQRS）

如果我们使用事件源，那么从事件存储中读取数据就变得困难了。要从数据存储中获取实体，我们需要处理所有的实体事件。有时我们对读写操作还会有不同的一致性和吞吐量要求。

这种情况，我们可以使用 CQRS 模式。在该模式中，系统的数据修改部分（命令）与数据读取部分（查询）是分离的。而 CQRS 模式有两种容易令人混淆的模式，分别是简单的和高级的。

在其简单形式中，不同实体或 ORM 模型被用于读写操作，如下所示：

![CQRS1](https://pic1.zhimg.com/80/v2-106c2746e37d811f6045674e9bb509d4_720w.webp?source=1940ef5c)

它有助于强化单一职责原则和分离关注点，从而实现更简洁的设计。

在其高级形式中，会有不同的数据存储用于读写操作。高级的 CQRS 通常结合事件源模式。根据不同情况，会使用不同类型的写数据存储和读数据存储。写数据存储是“记录的系统”，也就是整个系统的核心源头。

![CQRS2](https://pic1.zhimg.com/80/v2-ad75426e0e399c92bf47e3f50366c176_720w.webp?source=1940ef5c)

对于读频繁的应用程序或微服务架构，OLTP 数据库（任何提供 ACID 事务保证的关系或非关系数据库）或分布式消息系统都可以被用作写存储。对于写频繁的应用程序（写操作高可伸缩性和大吞吐量），需要使用写可水平伸缩的数据库（如全球托管的公共云数据库）。标准化的数据则保存在写数据存储中。

对搜索（例如 Apache Solr、Elasticsearch）或读操作（KV 数据库、文档数据库）进行优化的非关系数据库常被用作读存储。许多情况会在需要 SQL 查询的地方使用读可伸缩的关系数据库。非标准化和特殊优化过的数据则保存在读存储中。

数据是从写存储异步复制到读存储中的，所以读存储和写存储之间会有延迟，但最终是一致的。

优点

- 在事件驱动的微服务中数据读取速度更快。
- 数据的高可用性。
- 读写系统可独立扩展。

缺点

- 读数据存储是弱一致性的（最终一致性）。
- 整个系统的复杂性增加了，混乱的 CQRS 会显着危害整个项目。

何时使用 CQRS

- 在高可扩展的微服务架构中使用事件源。
- 在复杂领域模型中，读操作需要同时查询多个数据存储。
- 在读写操作负载差异明显的系统中。

何时不宜使用 CQRS

- 在没有必要存储大量事件的微服务架构中，用事件存储快照来计算实体状态是一个更好的选择。
- 在读写操作负载相近的系统中。

可用技术示例

- 写存储：EventStoreDB， Apache Kafka， Confluent Cloud， AWS Kinesis， Azure Event Hub， GCP Pub/Sub， Azure Cosmos DB， MongoDB， Cassandra. Amazon DynamoDB
- 读存储： Elastic Search， Solr， Cloud Spanner， Amazon Aurora， Azure Cosmos DB， Neo4j
- 框架： Lagom， Akka， Spring， akkatecture， Axon， Eventuate

延伸阅读bliki：CQRShttps://martinfowler.com/bliki/CQRS.html

CQRS模式 - Azure 架构中心https://docs.microsoft.com/en-us/azure/architecture/patterns/cqrs

微服务模式：命令和查询职责分离（CQRS）https://microservices.io/patter

[CQRS](https://zhuanlan.zhihu.com/p/115685384)

[cqrs-event-sourcing-java](https://www.baeldung.com/cqrs-event-sourcing-java)

