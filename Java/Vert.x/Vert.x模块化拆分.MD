 很好的问题！当接口多了之后，确实需要拆分。Vert.x 推荐用 **Router 模块化** + **Verticle 拆分** 来组织代码。

---

## 推荐项目结构

```
entropy-web/
├── src/main/java/com/entropy/entropy_web/
│   ├── MainVerticle.java              # 入口：组装所有路由，启动服务器
│   ├── verticles/                     # 业务 Verticle（可选）
│   │   └── DatabaseVerticle.java
│   ├── routers/                       # 路由模块
│   │   ├── UserRouter.java            # 用户模块路由
│   │   ├── OrderRouter.java           # 订单模块路由
│   │   └── HealthRouter.java          # 健康检查路由
│   ├── handlers/                      # 业务处理器（可选，复杂业务用）
│   │   ├── UserHandler.java
│   │   └── OrderHandler.java
│   ├── services/                      # 业务服务层
│   │   ├── UserService.java
│   │   └── OrderService.java
│   └── utils/                         # 工具类
│       └── ResponseUtil.java
```

---

## 方案 1：Router 模块化（推荐）

### 1. 用户模块路由 `UserRouter.java`

```java
package com.entropy.entropy_web.routers;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

public class UserRouter {
    
    private final Vertx vertx;
    
    public UserRouter(Vertx vertx) {
        this.vertx = vertx;
    }
    
    // 创建并返回配置好的 Router
    public Router createRouter() {
        Router router = Router.router(vertx);
        
        // 用户相关路由
        router.get("/").handler(this::listUsers);           // GET /api/users/
        router.get("/:id").handler(this::getUser);          // GET /api/users/:id
        router.post("/").handler(this::createUser);         // POST /api/users/
        router.put("/:id").handler(this::updateUser);       // PUT /api/users/:id
        router.delete("/:id").handler(this::deleteUser);    // DELETE /api/users/:id
        
        return router;
    }
    
    // ========== 处理器方法 ==========
    
    private void listUsers(RoutingContext ctx) {
        ctx.json(new JsonObject()
            .put("total", 2)
            .put("users", new JsonArray()
                .add(new JsonObject().put("id", "1").put("name", "张三"))
                .add(new JsonObject().put("id", "2").put("name", "李四"))
            )
        );
    }
    
    private void getUser(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        ctx.json(new JsonObject()
            .put("id", id)
            .put("name", "用户" + id)
            .put("email", "user" + id + "@example.com")
        );
    }
    
    private void createUser(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        ctx.response()
            .setStatusCode(201)
            .json(new JsonObject()
                .put("id", "10086")
                .put("name", body.getString("name"))
                .put("created", true)
            );
    }
    
    private void updateUser(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        JsonObject body = ctx.body().asJsonObject();
        ctx.json(new JsonObject()
            .put("id", id)
            .put("updated", true)
            .put("data", body)
        );
    }
    
    private void deleteUser(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        ctx.response()
            .setStatusCode(204)
            .end();
    }
}
```

---

### 2. 订单模块路由 `OrderRouter.java`

```java
package com.entropy.entropy_web.routers;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;

public class OrderRouter {
    
    private final Vertx vertx;
    
    public OrderRouter(Vertx vertx) {
        this.vertx = vertx;
    }
    
    public Router createRouter() {
        Router router = Router.router(vertx);
        
        router.get("/").handler(this::listOrders);
        router.get("/:orderId").handler(this::getOrder);
        router.post("/").handler(this::createOrder);
        router.get("/:orderId/items").handler(this::getOrderItems);
        
        return router;
    }
    
    private void listOrders(RoutingContext ctx) {
        ctx.json(new JsonObject().put("message", "订单列表"));
    }
    
    private void getOrder(RoutingContext ctx) {
        String orderId = ctx.pathParam("orderId");
        ctx.json(new JsonObject()
            .put("orderId", orderId)
            .put("status", "PAID")
            .put("amount", 199.99)
        );
    }
    
    private void createOrder(RoutingContext ctx) {
        ctx.response()
            .setStatusCode(201)
            .json(new JsonObject().put("orderId", "ORD-2024-001"));
    }
    
    private void getOrderItems(RoutingContext ctx) {
        String orderId = ctx.pathParam("orderId");
        ctx.json(new JsonObject()
            .put("orderId", orderId)
            .put("items", new io.vertx.core.json.JsonArray()
                .add(new JsonObject().put("name", "商品A").put("price", 99))
                .add(new JsonObject().put("name", "商品B").put("price", 100))
            )
        );
    }
}
```

---

### 3. 健康检查 `HealthRouter.java`

```java
package com.entropy.entropy_web.routers;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;

public class HealthRouter {
    
    private final Vertx vertx;
    private final long startTime;
    
    public HealthRouter(Vertx vertx) {
        this.vertx = vertx;
        this.startTime = System.currentTimeMillis();
    }
    
    public Router createRouter() {
        Router router = Router.router(vertx);
        
        router.get("/").handler(this::health);
        router.get("/ready").handler(this::ready);
        router.get("/live").handler(this::live);
        
        return router;
    }
    
    private void health(RoutingContext ctx) {
        long uptime = System.currentTimeMillis() - startTime;
        ctx.json(new JsonObject()
            .put("status", "UP")
            .put("uptime", uptime + "ms")
            .put("timestamp", System.currentTimeMillis())
        );
    }
    
    private void ready(RoutingContext ctx) {
        ctx.json(new JsonObject().put("ready", true));
    }
    
    private void live(RoutingContext ctx) {
        ctx.response().setStatusCode(200).end("OK");
    }
}
```

---

### 4. 主入口 `MainVerticle.java`（组装所有路由）

```java
package com.entropy.entropy_web;

import com.entropy.entropy_web.routers.HealthRouter;
import com.entropy.entropy_web.routers.OrderRouter;
import com.entropy.entropy_web.routers.UserRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ErrorHandler;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        
        // 1. 创建主 Router
        Router mainRouter = Router.router(vertx);
        
        // 2. 全局中间件（顺序很重要！）
        mainRouter.route().handler(CorsHandler.create("*"));  // 跨域
        mainRouter.route().handler(BodyHandler.create());      // 解析请求体
        
        // 3. 挂载子路由（模块化！）
        // 用户模块: /api/users/*
        mainRouter.mountSubRouter("/api/users", new UserRouter(vertx).createRouter());
        
        // 订单模块: /api/orders/*
        mainRouter.mountSubRouter("/api/orders", new OrderRouter(vertx).createRouter());
        
        // 健康检查: /health/*
        mainRouter.mountSubRouter("/health", new HealthRouter(vertx).createRouter());
        
        // 4. 404 处理（放在所有路由之后）
        mainRouter.route().handler(ctx -> {
            ctx.response()
                .setStatusCode(404)
                .end(new io.vertx.core.json.JsonObject()
                    .put("error", "Not Found")
                    .put("path", ctx.request().path())
                    .encode());
        });
        
        // 5. 错误处理
        mainRouter.errorHandler(500, ErrorHandler.create(vertx));
        
        // 6. 启动服务器
        int port = config().getInteger("http.port", 8888);
        vertx.createHttpServer()
            .requestHandler(mainRouter)
            .listen(port)
            .onSuccess(server -> {
                System.out.println("✅ Server started on port " + server.actualPort());
                System.out.println("📋 Available endpoints:");
                System.out.println("  GET  /api/users");
                System.out.println("  GET  /api/users/:id");
                System.out.println("  POST /api/users");
                System.out.println("  GET  /api/orders");
                System.out.println("  GET  /api/orders/:orderId");
                System.out.println("  GET  /health");
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }
}
```

---

## 方案 2：Handler 分离（业务复杂时用）

如果业务逻辑很复杂，可以把 Handler 再抽一层：

### `handlers/UserHandler.java`

```java
package com.entropy.entropy_web.handlers;

import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;
import com.entropy.entropy_web.services.UserService;

public class UserHandler {
    
    private final UserService userService;
    
    public UserHandler() {
        this.userService = new UserService();
    }
    
    public void list(RoutingContext ctx) {
        userService.findAll()
            .onSuccess(users -> ctx.json(users))
            .onFailure(err -> ctx.fail(500, err));
    }
    
    public void get(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        userService.findById(id)
            .onSuccess(user -> {
                if (user == null) {
                    ctx.response().setStatusCode(404).end("Not found");
                } else {
                    ctx.json(user);
                }
            });
    }
    
    public void create(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        userService.create(body)
            .onSuccess(result -> ctx.response().setStatusCode(201).json(result));
    }
}
```

### `services/UserService.java`

```java
package com.entropy.entropy_web.services;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

public class UserService {
    
    public Future<JsonObject> findById(String id) {
        Promise<JsonObject> promise = Promise.promise();
        // 模拟数据库查询
        promise.complete(new JsonObject()
            .put("id", id)
            .put("name", "用户" + id)
        );
        return promise.future();
    }
    
    public Future<JsonArray> findAll() {
        Promise<JsonArray> promise = Promise.promise();
        promise.complete(new JsonArray()
            .add(new JsonObject().put("id", "1"))
            .add(new JsonObject().put("id", "2"))
        );
        return promise.future();
    }
    
    public Future<JsonObject> create(JsonObject data) {
        Promise<JsonObject> promise = Promise.promise();
        data.put("id", "10086");
        promise.complete(data);
        return promise.future();
    }
}
```

---

## 路由 vs Handler vs Service 职责划分

| 层级 | 职责 | 示例 |
|------|------|------|
| **Router** | 定义路由规则，调用 Handler | `router.get("/:id").handler(handler::get)` |
| **Handler** | 处理 HTTP 请求/响应，参数校验 | 提取 pathParam，调用 Service，返回 JSON |
| **Service** | 业务逻辑，数据操作 | 查询数据库，计算，调用外部 API |
| **DAO/Repository** | 数据库访问（可选） | SQL 执行，Redis 操作 |

---

## 测试新结构

```bash
# 用户模块
curl http://localhost:8888/api/users
curl http://localhost:8888/api/users/123
curl -X POST http://localhost:8888/api/users -H "Content-Type: application/json" -d '{"name":"张三"}'

# 订单模块
curl http://localhost:8888/api/orders
curl http://localhost:8888/api/orders/ORD-001
curl http://localhost:8888/api/orders/ORD-001/items

# 健康检查
curl http://localhost:8888/health
curl http://localhost:8888/health/ready
```

---

## 快速检查清单

- [ ] 创建 `routers/` 包
- [ ] 每个模块一个 `XxxRouter.java`
- [ ] Router 提供 `createRouter()` 方法返回 `Router`
- [ ] `MainVerticle` 用 `mountSubRouter()` 挂载
- [ ] 全局中间件（BodyHandler、CorsHandler）放在挂载之前
- [ ] 404 处理放在所有路由之后

需要我展示 **数据库集成**（MySQL/PostgreSQL）或者 **JWT 认证中间件** 的模块化写法吗？
