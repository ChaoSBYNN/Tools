An expected CSRF token cannot be found

因为mvc和gateway冲突，而配置了：`spring.main.web-application-type=reactive`

```java
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }
```
