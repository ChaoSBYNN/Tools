# Strangler

## 神奇的生物

Strangler Fig 原本是生长在热带雨林中的一种攀缘植物，中文是绞杀植物(或杀手树）

> “ 指一种植物以附生来开始它的生长，然后通过根茎的成长成为独立生活的植物，并采用挤压、攀抱、缠绕等方式盘剥寄树营养，剥夺寄树的生存空间，从而杀死寄树。—— Wikipedia”

![Strangler Fig](https://pic4.zhimg.com/80/v2-ee1e1a3b94a7ac5d7df4aaea86278087_720w.webp)

## Strangler Fig 设计模式

如果想在运行中的项目中使用微服务架构，我们需要将遗留的或现有的单体应用迁移到微服务。将现有的大型在线单体应用程序迁移到微服务是相当有挑战性的，因为这可能破坏应用程序的可用性。

一个解决方案是使用 Strangler 模式。Strangler 模式意味着通过使用新的微服务逐步替换特定功能，将单体应用程序增量地迁移到微服务架构。此外，新功能只在微服务中添加，而不再添加到遗留的单体应用中。然后配置一个 Facade （API 网关）来路由遗留单体应用和微服务间的请求。当某个功能从单体应用迁移到微服务，Facade 就会拦截客户端请求并路由到新的微服务。一旦迁移了所有的功能，遗留单体应用程序就会被“扼杀（Strangler）”，即退役。

![Stangler](https://pic1.zhimg.com/80/v2-7cfa78b8a5248d5da38c4982e4b5c942_720w.webp?source=1940ef5c)

![Stangler2](https://pic1.zhimg.com/80/v2-190d130e64d2abd8f092f74bd30b8f44_720w.webp)

### 为何不彻底开发新的再替换

- 切换前新系统的能力无法得到渐进的全面的验证，重大的切换将带来不可控风险；
- 完整的新系统开发周期较长，交付和反馈更慢，容易使得迁移半途而废；
- 开发过程中可能需要同时在新旧两套系统中同时加入新功能一满足新到达的需求；
- 新旧系统如果不能一次切换，采用同时提供服务，将引入更多的复杂性，于此同时，由于调用方缺少迁移的动力导致旧系统持续存在很难下线的问题发生。
- 使用 Strangler Fig 模式可以轻松的做到迁移的同时引入新的功能，在老板看来事情会更有合理性，当然也的确合理。（要记住迁移和重构往往意味着 ROI 很难评估的工作）

### 需要注意的问题

- 如果新旧系统都需要同时调用某个服务，或者 CURD 某个数据库实体。需要保证在特定闭环资源迁移完成前，新旧系统都可以并行的访问操作相关的资源；
- 对于服务的调用方来说，如果在使用 Strangler Fig 的过程中，不能屏蔽新旧两个入口，那该模式将难以开展。可以考虑通过负载均衡器、请求拦截器等方式解决该问题，以使调用方无感知迁移过程；
- 迁移完成后需要考虑 Strangler Fig 过程中的统一入口如何解决；
- 需要确保统一入口处不会成为故障单点或者性能瓶颈点；

### 优点

- 安全的迁移单体应用程序到微服务。
- 可以并行地迁移已有功能和开发新功能。
- 迁移过程可以更好把控节奏。

### 缺点

- 在现有的单体应用服务和新的微服务之间共享数据存储变得具有挑战性。
- 添加 Facade （API 网关）将增加系统延迟。
- 端到端测试变得困难。

### 何时使用 Strangler

- 将大型后端单体应用程序的增量迁移到微服务。

### 何时不宜使用 Strangler

- 如果后端单体应用很小，那么全量替换会更好。
- 如果无法拦截客户端对遗留的单体应用程序的请求。

### 可用技术示例

- API 网关后端应用框架。

### 延伸阅读

- bliki：StranglerFig 应用程序https://martinfowler.com/bliki/StranglerFigApplication.html
- Strangler 模式 - 云设计模式https://docs.microsoft.com/en-us/azure/architecture/patterns/strangler-fig
- 微服务模式：Strangler 应用程序https://microservices.io/patter
