# TODO

* @RequestBody
* @RequestParam
* @PathVariable

## springboot启动时排除一些不需要注入的组件

```java 
  @SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
  @ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ResourcesConfig.class,
         RedisConfig.class, ShiroConfig.class, FilterConfig.class, LoginService.class, PasswordService.class}))
```

* `@SpringBootApplication:exclude` 只能排除 `@EnableAutoConfiguration`
* `@ComponentScan:excludeFilters` 扫描全部注解
