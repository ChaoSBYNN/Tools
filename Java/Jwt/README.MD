# Jwt 加解密

* [jwt.io](https://jwt.io/)

```xml
        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
            <version>4.4.0</version>
        </dependency>
```

```java
        Calendar instance = Calendar.getInstance();
        // 默认30 s
        instance.add(Calendar.SECOND, 30);

        // 头部map
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ", "jwt");
        headerMap.put("alg", "sha256");

        String token = JWT.create()
                .withHeader(headerMap)      // 头部, 默认使用 sha256 算法
                .withIssuer("Entropy")    // 签发者
                .withIssuedAt(Instant.now())  // 签发时间
                .withExpiresAt(instance.getTime())        // 有效时间
                .withSubject("daji_yang@163.com")   // 面向用户
                .withAudience("www.baidu.com","www.google.com")  // 接收方
                .withNotBefore(Instant.now())           // 定义某个时间前都不可用
                .withJWTId("dddddddd")      // 唯一身份标识
                .withClaim("userId", 11)     // palyload
                .withClaim("username", "guest") // palyload
                .sign(Algorithm.HMAC256("Spike"));

        System.out.println(token);


        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("Spike")).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        System.out.println(decodedJWT.toString());
        System.out.println(decodedJWT.getHeader().toString());
        System.out.println(decodedJWT.getClaim("userId"));
        System.out.println(decodedJWT.getClaim("username"));
```
