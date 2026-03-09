太好了！现在教你写 HTTP 请求，Vert.x 提供两种方式：**服务端接收请求**（你已经会了）和 **客户端发送请求**。

---

## 方式一：服务端路由（接收 HTTP 请求）

在你的 `MainVerticle.java` 中添加各种路由：

```java
package com.entropy.entropy_web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        
        // 解析请求体（必须放在路由之前）
        router.route().handler(BodyHandler.create());

        // ========== GET 请求 ==========
        router.get("/api/hello").handler(this::hello);
        router.get("/api/users/:id").handler(this::getUser);
        router.get("/api/users").handler(this::listUsers);

        // ========== POST 请求 ==========
        router.post("/api/users").handler(this::createUser);

        // ========== PUT 请求 ==========
        router.put("/api/users/:id").handler(this::updateUser);

        // ========== DELETE 请求 ==========
        router.delete("/api/users/:id").handler(this::deleteUser);

        // 启动服务器
        int port = config().getInteger("http.port", 8888);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(port)
            .onSuccess(server -> {
                System.out.println("✅ HTTP server started on port " + server.actualPort());
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }

    // GET /api/hello
    private void hello(RoutingContext ctx) {
        ctx.json(new JsonObject()
            .put("message", "Hello Vert.x!")
            .put("timestamp", System.currentTimeMillis())
        );
    }

    // GET /api/users/:id
    private void getUser(RoutingContext ctx) {
        String userId = ctx.pathParam("id");
        
        ctx.json(new JsonObject()
            .put("id", userId)
            .put("name", "张三")
            .put("email", "zhangsan@example.com")
            .put("age", 25)
        );
    }

    // GET /api/users
    private void listUsers(RoutingContext ctx) {
        JsonArray users = new JsonArray()
            .add(new JsonObject().put("id", "1").put("name", "张三"))
            .add(new JsonObject().put("id", "2").put("name", "李四"))
            .add(new JsonObject().put("id", "3").put("name", "王五"));
        
        ctx.json(new JsonObject()
            .put("total", 3)
            .put("users", users)
        );
    }

    // POST /api/users
    private void createUser(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String name = body.getString("name");
        String email = body.getString("email");
        
        System.out.println("创建用户: " + name + ", " + email);
        
        ctx.response()
            .setStatusCode(201)
            .end(new JsonObject()
                .put("id", "10086")
                .put("name", name)
                .put("email", email)
                .put("created", true)
                .encode());
    }

    // PUT /api/users/:id
    private void updateUser(RoutingContext ctx) {
        String userId = ctx.pathParam("id");
        JsonObject body = ctx.body().asJsonObject();
        
        ctx.json(new JsonObject()
            .put("id", userId)
            .put("updated", true)
            .put("data", body)
        );
    }

    // DELETE /api/users/:id
    private void deleteUser(RoutingContext ctx) {
        String userId = ctx.pathParam("id");
        
        ctx.response()
            .setStatusCode(204)  // No Content
            .end();
    }
}
```

---

## 方式二：客户端发送 HTTP 请求

创建 `HttpClientVerticle.java`：

```java
package com.entropy.entropy_web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;

public class HttpClientVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        WebClient client = WebClient.create(vertx);
        
        // ========== GET 请求 ==========
        client.get(8888, "localhost", "/api/hello")
            .send()
            .onSuccess(res -> {
                System.out.println("GET 响应: " + res.bodyAsString());
            })
            .onFailure(err -> {
                System.err.println("GET 失败: " + err.getMessage());
            });

        // ========== POST 请求（带 JSON 体）==========
        JsonObject newUser = new JsonObject()
            .put("name", "新用户")
            .put("email", "new@example.com");

        client.post(8888, "localhost", "/api/users")
            .putHeader("Content-Type", "application/json")
            .sendJsonObject(newUser)
            .onSuccess(res -> {
                System.out.println("POST 响应: " + res.bodyAsString());
            });

        // ========== 带查询参数的 GET ==========
        client.get(8888, "localhost", "/api/search")
            .addQueryParam("keyword", "vertx")
            .addQueryParam("page", "1")
            .send()
            .onSuccess(res -> {
                System.out.println("搜索响应: " + res.bodyAsString());
            });

        // ========== 异步链式调用 ==========
        // 先登录，再获取用户信息
        client.post(8888, "localhost", "/api/login")
            .sendJsonObject(new JsonObject().put("username", "admin").put("password", "123456"))
            .compose(loginRes -> {
                String token = loginRes.bodyAsJsonObject().getString("token");
                // 用 token 请求用户信息
                return client.get(8888, "localhost", "/api/profile")
                    .putHeader("Authorization", "Bearer " + token)
                    .send();
            })
            .onSuccess(profileRes -> {
                System.out.println("用户信息: " + profileRes.bodyAsString());
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }
}
```

**需要在 `pom.xml` 添加依赖：**

```xml
<dependency>
  <groupId>io.vertx</groupId>
  <artifactId>vertx-web-client</artifactId>
</dependency>
```

---

## 方式三：使用原生 HttpClient（更底层）

```java
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;

HttpClient client = vertx.createHttpClient();

// GET 请求
client.request(HttpMethod.GET, 8888, "localhost", "/api/hello")
    .compose(req -> req.send().compose(HttpClientResponse::body))
    .onSuccess(body -> {
        System.out.println("响应: " + body.toString());
    });

// POST 请求
client.request(HttpMethod.POST, 8888, "localhost", "/api/users")
    .compose(req -> {
        req.putHeader("Content-Type", "application/json");
        return req.send(JsonObject.of("name", "test").toBuffer())
            .compose(HttpClientResponse::body);
    })
    .onSuccess(body -> {
        System.out.println("创建成功: " + body.toString());
    });
```

---

## 测试你的 API

启动服务器后，用浏览器或 curl 测试：

```bash
# GET 请求
curl http://localhost:8888/api/hello
curl http://localhost:8888/api/users/123
curl http://localhost:8888/api/users

# POST 请求
curl -X POST http://localhost:8888/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","email":"zs@example.com"}'

# PUT 请求
curl -X PUT http://localhost:8888/api/users/123 \
  -H "Content-Type: application/json" \
  -d '{"name":"李四"}'

# DELETE 请求
curl -X DELETE http://localhost:8888/api/users/123
```

---

## 快速参考表

| 操作 | 服务端（Router） | 客户端（WebClient） |
|------|----------------|-------------------|
| **GET** | `router.get("/path").handler(...)` | `client.get(port, host, uri).send()` |
| **POST** | `router.post("/path").handler(...)` | `client.post(...).sendJsonObject(json)` |
| **PUT** | `router.put("/path").handler(...)` | `client.put(...).sendBuffer(buffer)` |
| **DELETE** | `router.delete("/path").handler(...)` | `client.delete(...).send()` |
| **路径参数** | `ctx.pathParam("id")` | 直接写在 uri 中 `/users/123` |
| **查询参数** | `ctx.queryParams().get("name")` | `.addQueryParam("key", "value")` |
| **请求体** | `ctx.body().asJsonObject()` | `.sendJsonObject(json)` |
| **响应** | `ctx.json(obj)` 或 `ctx.end(string)` | `res.bodyAsString()` / `res.bodyAsJsonObject()` |

需要我展示 **文件上传下载**、**WebSocket**、或者 **连接数据库** 的示例吗？

太好了！既然 `/api/users/:id` 能跑通，说明你的 Vert.x 服务已经正常运行。接下来我给你几个进阶方向，你可以按需选择：

---

## 1. 添加请求参数处理（查询参数 + 请求头）

```java
// GET /api/users?id=123&name=张三  或  /api/users/123
router.get("/api/users/:id?").handler(ctx -> {
    // 路径参数 /api/users/123
    String pathId = ctx.pathParam("id");
    
    // 查询参数 /api/users?id=123
    String queryId = ctx.queryParams().get("id");
    String name = ctx.queryParams().get("name");
    
    // 请求头
    String authHeader = ctx.request().getHeader("Authorization");
    String userAgent = ctx.request().getHeader("User-Agent");
    
    // 实际使用的ID（优先路径参数）
    String finalId = pathId != null ? pathId : queryId;
    
    ctx.json(new JsonObject()
        .put("id", finalId)
        .put("name", name != null ? name : "未知")
        .put("auth", authHeader != null ? "已授权" : "未授权")
    );
});
```

---

## 2. 添加请求验证（参数校验）

```java
// 验证中间件
router.get("/api/users/:id").handler(ctx -> {
    String id = ctx.pathParam("id");
    
    // 参数验证
    if (id == null || id.isEmpty()) {
        ctx.response()
            .setStatusCode(400)
            .end(new JsonObject()
                .put("error", "ID不能为空")
                .put("code", "BAD_REQUEST")
                .encode());
        return;  // 必须return，否则继续执行
    }
    
    // 数字验证
    try {
        Integer.parseInt(id);
    } catch (NumberFormatException e) {
        ctx.response()
            .setStatusCode(400)
            .end(new JsonObject()
                .put("error", "ID必须是数字")
                .encode());
        return;
    }
    
    // 验证通过，继续处理
    ctx.next();  // 交给下一个handler
});

// 实际处理
router.get("/api/users/:id").handler(ctx -> {
    String id = ctx.pathParam("id");
    ctx.json(new JsonObject()
        .put("id", id)
        .put("name", "用户" + id)
    );
});
```

---

## 3. 模拟数据库操作（异步）

```java
// 内存数据库
private Map<String, JsonObject> users = new HashMap<>();

@Override
public void start(Promise<Void> startPromise) {
    // 初始化一些数据
    users.put("1", new JsonObject().put("id", "1").put("name", "张三").put("age", 25));
    users.put("2", new JsonObject().put("id", "2").put("name", "李四").put("age", 30));
    
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    
    // CRUD 路由
    router.get("/api/users").handler(this::listUsers);
    router.get("/api/users/:id").handler(this::getUser);
    router.post("/api/users").handler(this::createUser);
    router.put("/api/users/:id").handler(this::updateUser);
    router.delete("/api/users/:id").handler(this::deleteUser);
    
    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8888)
        .onSuccess(server -> {
            System.out.println("✅ Server ready at http://localhost:8888");
            System.out.println("可用端点:");
            System.out.println("  GET    /api/users       列表");
            System.out.println("  GET    /api/users/:id   详情");
            System.out.println("  POST   /api/users       创建");
            System.out.println("  PUT    /api/users/:id   更新");
            System.out.println("  DELETE /api/users/:id   删除");
            startPromise.complete();
        });
}

// 查询单个（异步模拟）
private void getUser(RoutingContext ctx) {
    String id = ctx.pathParam("id");
    
    // 模拟异步数据库查询
    vertx.executeBlocking(promise -> {
        // 模拟耗时操作
        try {
            Thread.sleep(10);  // 模拟10ms延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        promise.complete(users.get(id));
    }).onSuccess(result -> {
        if (result == null) {
            ctx.response()
                .setStatusCode(404)
                .end(new JsonObject()
                    .put("error", "用户不存在")
                    .put("id", id)
                    .encode());
        } else {
            ctx.json((JsonObject) result);
        }
    }).onFailure(err -> {
        ctx.fail(500, err);
    });
}

// 创建用户
private void createUser(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    String id = UUID.randomUUID().toString().substring(0, 8);
    
    JsonObject newUser = new JsonObject()
        .put("id", id)
        .put("name", body.getString("name", "未命名"))
        .put("email", body.getString("email", ""))
        .put("createdAt", System.currentTimeMillis());
    
    users.put(id, newUser);
    
    ctx.response()
        .setStatusCode(201)
        .end(new JsonObject()
            .put("success", true)
            .put("data", newUser)
            .encode());
}

// 列表（带分页）
private void listUsers(RoutingContext ctx) {
    String pageStr = ctx.queryParams().get("page");
    String sizeStr = ctx.queryParams().get("size");
    
    int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
    int size = sizeStr != null ? Integer.parseInt(sizeStr) : 10;
    
    List<JsonObject> list = new ArrayList<>(users.values());
    int total = list.size();
    int fromIndex = (page - 1) * size;
    int toIndex = Math.min(fromIndex + size, total);
    
    List<JsonObject> pageData = fromIndex < total ? 
        list.subList(fromIndex, toIndex) : new ArrayList<>();
    
    ctx.json(new JsonObject()
        .put("total", total)
        .put("page", page)
        .put("size", size)
        .put("data", new JsonArray(pageData))
    );
}
```

---

## 4. 统一错误处理

```java
// 放在所有路由之后
router.errorHandler(404, ctx -> {
    ctx.response()
        .setStatusCode(404)
        .putHeader("Content-Type", "application/json")
        .end(new JsonObject()
            .put("error", "Not Found")
            .put("path", ctx.request().path())
            .put("method", ctx.request().method())
            .encode());
});

router.errorHandler(500, ctx -> {
    Throwable failure = ctx.failure();
    ctx.response()
        .setStatusCode(500)
        .end(new JsonObject()
            .put("error", "Internal Server Error")
            .put("message", failure != null ? failure.getMessage() : "Unknown")
            .encode());
});
```

---

## 5. 测试命令

```bash
# 1. 获取列表
curl http://localhost:8888/api/users

# 2. 分页查询
curl "http://localhost:8888/api/users?page=1&size=5"

# 3. 获取单个
curl http://localhost:8888/api/users/1

# 4. 创建用户
curl -X POST http://localhost:8888/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"王五","email":"wangwu@example.com"}'

# 5. 更新用户
curl -X PUT http://localhost:8888/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"张三（已修改）"}'

# 6. 删除用户
curl -X DELETE http://localhost:8888/api/users/2

# 7. 访问不存在的用户（测试404）
curl http://localhost:8888/api/users/9999
```

---

## 下一步推荐

| 方向 | 难度 | 说明 |
|------|------|------|
| **连接真实数据库**（MySQL/PostgreSQL/MongoDB） | ⭐⭐ | 用 Vert.x 异步客户端 |
| **添加 JWT 认证** | ⭐⭐⭐ | 登录鉴权 |
| **文件上传下载** | ⭐⭐ | 图片/文档处理 |
| **WebSocket 实时通信** | ⭐⭐⭐ | 聊天室/推送 |
| **部署到服务器/Docker** | ⭐⭐ | 生产环境 |

你想先玩哪个？我可以给你完整代码。
