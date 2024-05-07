# MongoDB 

```xml

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
```

```yml
spring:
    data:
        mongodb:
            uri: mongodb://${username}:${password}@${ip}:27017/${db}
```
