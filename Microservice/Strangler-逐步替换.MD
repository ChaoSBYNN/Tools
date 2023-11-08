# Strangler

如果想在运行中的项目中使用微服务架构，我们需要将遗留的或现有的单体应用迁移到微服务。将现有的大型在线单体应用程序迁移到微服务是相当有挑战性的，因为这可能破坏应用程序的可用性。

一个解决方案是使用 Strangler 模式。Strangler 模式意味着通过使用新的微服务逐步替换特定功能，将单体应用程序增量地迁移到微服务架构。此外，新功能只在微服务中添加，而不再添加到遗留的单体应用中。然后配置一个 Facade （API 网关）来路由遗留单体应用和微服务间的请求。当某个功能从单体应用迁移到微服务，Facade 就会拦截客户端请求并路由到新的微服务。一旦迁移了所有的功能，遗留单体应用程序就会被“扼杀（Strangler）”，即退役。

![Stangler](https://pic1.zhimg.com/80/v2-7cfa78b8a5248d5da38c4982e4b5c942_720w.webp?source=1940ef5c)

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

#### 何时不宜使用 Strangler

- 如果后端单体应用很小，那么全量替换会更好。
- 如果无法拦截客户端对遗留的单体应用程序的请求。

### 可用技术示例

- API 网关后端应用框架。

### 延伸阅读

- bliki：StranglerFig 应用程序https://martinfowler.com/bliki/StranglerFigApplication.html
- Strangler 模式 - 云设计模式https://docs.microsoft.com/en-us/azure/architecture/patterns/strangler-fig
- 微服务模式：Strangler 应用程序https://microservices.io/patter
