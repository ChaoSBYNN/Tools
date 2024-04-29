# 

* https://blog.51cto.com/u_16175451/6630240
* https://blog.csdn.net/weixin_42575806/article/details/131513847
* https://www.cnblogs.com/JackyGz/p/17952920

* [【基础】MQTT -- MQTT 特性：QoS、Retained 消息、LWT 以及 Keepalive](https://blog.csdn.net/zqf787351070/article/details/131331779)

```xml
    <!-- mqtt -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-integration</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.integration</groupId>
      <artifactId>spring-integration-stream</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.integration</groupId>
      <artifactId>spring-integration-mqtt</artifactId>
    </dependency>
```

```yml
mqtt:
  hostUrl: tcp://127.0.0.1:1883
  username: admin
  password: HelloWorld42@
  client-id: MQTT-CLIENT-DEV
  cleanSession: true
  reconnect: true
  timeout: 100
  keepAlive: 100
  defaultTopic: testtopic
  serverTopic: testtopic
  isOpen: true
  qos: 0

# 循环引用配值
spring:
  main:
    allow-circular-references: true
```
