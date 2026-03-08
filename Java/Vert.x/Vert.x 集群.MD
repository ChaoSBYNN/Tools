Vert.x 集群模式需要改造几个关键部分：集群管理器（Hazelcast/Infinispan）、Event Bus 集群通信、共享数据、以及部署策略。以下是完整改造方案：

---

## 1. 添加集群依赖（pom.xml）

```xml
<properties>
    <vertx.version>4.5.25</vertx.version>
    <hazelcast.version>5.3.6</hazelcast.version>
</properties>

<dependencies>
    <!-- Vert.x 核心集群 -->
    <dependency>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-hazelcast</artifactId>
        <version>${vertx.version}</version>
    </dependency>
    
    <!-- 或者使用 Infinispan（性能更好） -->
    <!--
    <dependency>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-infinispan</artifactId>
        <version>${vertx.version}</version>
    </dependency>
    -->
    
    <!-- Hazelcast 配置 -->
    <dependency>
        <groupId>com.hazelcast</groupId>
        <artifactId>hazelcast</artifactId>
        <version>${hazelcast.version}</version>
    </dependency>
</dependencies>
```

---

## 2. 集群启动入口（ClusteredMain.java）

```java
package com.entropy.entropy_web;

import com.entropy.entropy_web.routers.HealthRouter;
import com.entropy.entropy_web.routers.OrderRouter;
import com.entropy.entropy_web.routers.UserRouter;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;

public class ClusteredMain {
    
    public static void main(String[] args) {
        
        // 1. 配置 Hazelcast
        Config hazelcastConfig = new Config();
        hazelcastConfig.setClusterName("entropy-cluster");
        
        // 网络配置
        NetworkConfig networkConfig = hazelcastConfig.getNetworkConfig();
        networkConfig.setPort(5701)  // Hazelcast 通信端口
            .setPortAutoIncrement(true);
        
        // 集群发现方式（开发环境：TCP-IP，生产环境：Kubernetes DNS 或 AWS）
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);  // 关闭组播
        
        // TCP-IP 方式（指定集群节点 IP）
        joinConfig.getTcpIpConfig()
            .setEnabled(true)
            .addMember("192.168.1.101")  // 节点1
            .addMember("192.168.1.102")  // 节点2
            .addMember("192.168.1.103"); // 节点3
        
        // 2. 创建集群管理器
        ClusterManager clusterManager = new HazelcastClusterManager(hazelcastConfig);
        
        // 3. 配置 Vert.x 选项
        VertxOptions options = new VertxOptions()
            .setClusterManager(clusterManager)
            .setEventBusOptions(new io.vertx.core.eventbus.EventBusOptions()
                .setHost("192.168.1.101")  // 本机 IP（其他节点改自己的 IP）
                .setPort(7000))             // Event Bus 端口
            .setHAEnabled(true)              // 启用高可用
            .setHAGroup("entropy-web");      // HA 分组
        
        // 4. 集群化启动 Vert.x
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                System.out.println("✅ 节点加入集群成功！");
                
                // 5. 部署主 Verticle（多个实例）
                DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setInstances(4)  // 每个节点启动 4 个实例（利用多核）
                    .setHa(true);      // 启用 HA 故障转移
                
                vertx.deployVerticle(MainVerticle.class.getName(), deploymentOptions, deployRes -> {
                    if (deployRes.succeeded()) {
                        System.out.println("✅ MainVerticle 部署成功: " + deployRes.result());
                    } else {
                        System.err.println("❌ 部署失败: " + deployRes.cause().getMessage());
                    }
                });
                
                // 6. 部署其他 Verticle（如消息消费者）
                vertx.deployVerticle(MessageConsumerVerticle.class.getName(), 
                    new DeploymentOptions().setInstances(2));
                
            } else {
                System.err.println("❌ 集群启动失败: " + res.cause().getMessage());
            }
        });
    }
}
```

---

## 3. 改造 MainVerticle（支持集群）

```java
package com.entropy.entropy_web;

import com.entropy.entropy_web.routers.HealthRouter;
import com.entropy.entropy_web.routers.OrderRouter;
import com.entropy.entropy_web.routers.UserRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {
    
    @Override
    public void start(Promise<Void> startPromise) {
        
        // 获取集群 Event Bus
        EventBus eventBus = vertx.eventBus();
        System.out.println("🚀 Verticle 启动，部署ID: " + context.deploymentID());
        
        // 1. 创建 Router
        Router mainRouter = Router.router(vertx);
        mainRouter.route().handler(BodyHandler.create());
        
        // 2. 挂载子路由
        mainRouter.mountSubRouter("/api/users", new UserRouter(vertx, eventBus).createRouter());
        mainRouter.mountSubRouter("/api/orders", new OrderRouter(vertx, eventBus).createRouter());
        mainRouter.mountSubRouter("/health", new HealthRouter(vertx).createRouter());
        
        // 3. 集群状态端点
        mainRouter.get("/cluster/info").handler(ctx -> {
            ctx.json(new io.vertx.core.json.JsonObject()
                .put("nodeId", vertx.getOrCreateContext().deploymentID())
                .put("isClustered", vertx.isClustered())
                .put("instance", System.getProperty("vertx.instance", "unknown"))
            );
        });
        
        // 4. 启动 HTTP 服务器（端口冲突处理）
        int basePort = config().getInteger("http.port", 8888);
        // 集群模式下，每个实例用不同端口，或用随机端口
        int port = basePort + (int)(Math.random() * 100);  // 简单示例
        
        vertx.createHttpServer()
            .requestHandler(mainRouter)
            .listen(port)
            .onSuccess(server -> {
                System.out.println("✅ HTTP Server started on port " + server.actualPort());
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }
}
```

---

## 4. Event Bus 通信（集群核心）

### 消息消费者 `MessageConsumerVerticle.java`

```java
package com.entropy.entropy_web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class MessageConsumerVerticle extends AbstractVerticle {
    
    @Override
    public void start(Promise<Void> startPromise) {
        EventBus eventBus = vertx.eventBus();
        
        // 消费者1：处理用户相关消息
        eventBus.consumer("user.get", message -> {
            JsonObject params = (JsonObject) message.body();
            String userId = params.getString("id");
            
            System.out.println("[" + context.deploymentID() + "] 处理用户查询: " + userId);
            
            // 模拟数据库查询
            JsonObject result = new JsonObject()
                .put("id", userId)
                .put("name", "用户" + userId)
                .put("handledBy", context.deploymentID());
            
            message.reply(result);
        });
        
        // 消费者2：处理订单相关消息
        eventBus.consumer("order.create", message -> {
            JsonObject orderData = (JsonObject) message.body();
            System.out.println("创建订单: " + orderData.encode());
            
            // 广播通知其他节点
            eventBus.publish("order.created", orderData);
            
            message.reply(new JsonObject().put("status", "created"));
        });
        
        // 广播消费者：监听订单创建事件
        eventBus.consumer("order.created", message -> {
            System.out.println("[" + context.deploymentID() + "] 收到订单创建广播: " + message.body());
        });
        
        startPromise.complete();
    }
}
```

---

## 5. Router 中使用 Event Bus

```java
package com.entropy.entropy_web.routers;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class UserRouter {
    
    private final Vertx vertx;
    private final EventBus eventBus;
    
    public UserRouter(Vertx vertx, EventBus eventBus) {
