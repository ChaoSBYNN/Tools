# 面向前端的后端 （BFF）

在现代商业应用开发，特别是微服务架构中，前后端应用是分离和独立的服务，它们通过 API 或 GraphQL 连接。如果应用程序还有移动 App 客户端，那么 Web 端和移动客户端使用相同的后端微服务就会出现问题。因为移动客户端和 Web 客户端有不同的屏幕尺寸、显示屏、性能、能耗和网络带宽，它们的 API 需求不同。

面向前端的后端模式适用于需要为特殊 UI 定制单独后端的场景。它还提供了其他优势，比如作为下游微服务的封装，从而减少 UI 和下游微服务之间的频繁通信。此外，在高安全要求的场景中，BFF 为部署在 DMZ 网络中的下游微服务提供了更高的安全性。

![BFF1](https://picx.zhimg.com/80/v2-9d7d633aeb10cd53d5ecbe2d980c1f35_720w.webp?source=1940ef5c)

优点

- 分离 BFF 之间的关注点，使得我们可以为具体的 UI 优化他们。
- 提供更高的安全性。
- 减少 UI 和下游微服务之间频繁的通信。

缺点

- BFF 之间代码重复。
- 大量的 BFF 用于其他用户界面（例如，智能电视，Web，移动端，PC 桌面版）。
- 需要仔细的设计和实现，BFF 不应该包含任何业务逻辑，而应只包含特定客户端逻辑和行为。

何时使用 BFF

- 如果应用程序有多个含不同 API 需求的 UI。
- 出于安全需要，UI 和下游微服务之间需要额外的层。
- 如果在 UI 开发中使用微前端。

何时不宜使用 BFF

- 如果应用程序虽有多个 UI，但使用的 API 相同。
- 如果核心微服务不是部署在 DMZ 网络中。

可用技术示例

任何后端框架（Node.js，Spring，Django，Laravel，Flask，Play，…）都能支持。

- 延伸阅读Sam Newman - 面向前端的后端https://samnewman.io/patterns/architectural/bff/
- 面向前端的后端模式 - 云设计模式https://docs.microsoft.com/en-us/azure/architecture/patterns/backends-for-frontends
- 微服务模式：API 网关模式https://microservices.io/patter
