# The Repository Pattern

> https://zhuanlan.zhihu.com/p/348706530

## Why 为什么要用 Repository

### 实体模型 vs. 贫血模型

Entity（实体）这个词在计算机领域的最初应用可能是来自于Peter Chen在1976年的“The Entity-Relationship Model - Toward a Unified View of Data"（ER模型），用来描述实体之间的关系，而ER模型后来逐渐的演变成为一个数据模型，在关系型数据库中代表了数据的储存方式。而2006年的JPA标准，通过@Entity等注解，以及Hibernate等ORM框架的实现，让很多Java开发对Entity的理解停留在了数据映射层面，忽略了Entity实体的本身行为，造成今天很多的模型仅包含了实体的数据和属性，而所有的业务逻辑都被分散在多个服务、Controller、Utils工具类中，这个就是Martin Fowler所说的的Anemic Domain Model（贫血领域模型）。如何知道你的模型是贫血的呢？可以看一下你代码中是否有以下的几个特征：

- 有大量的XxxDO对象：这里DO虽然有时候代表了Domain Object，但实际上仅仅是数据库表结构的映射，里面没有包含（或包含了很少的）业务逻辑；
- 服务和Controller里有大量的业务逻辑：比如校验逻辑、计算逻辑、格式转化逻辑、对象关系逻辑、数据存储逻辑等；
- 大量的Utils工具类等。

