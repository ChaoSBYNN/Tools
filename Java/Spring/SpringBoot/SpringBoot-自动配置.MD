# Spring Boot 自动配置

> 根据应用程序的类路径和已有的配置，自动配置各种常用的功能，从而减少了手动配置的工作量。

## What

通过  `@EnableAutoConfiguration`  注解来实现的。当 Spring Boot 应用程序启动时，它会扫描  `@EnableAutoConfiguration`  注解标注的类，并根据这些类的配置，自动配置各种常用的功能。 

Spring Boot 自动配置的核心是  Spring Boot Starter  项目。Spring Boot Starter 是一个预先配置好的依赖项集合，它可以快速启动各种常见的功能和技术栈，如 Web 开发、数据库访问、安全性等。 

## How

常见用法：

- 自动配置嵌入式容器：Spring Boot 提供了多个嵌入式容器，如 Tomcat、Jetty 和 Undertow。当你在 Spring Boot 应用程序中添加一个 Spring Boot Starter 依赖项时，Spring Boot 会自动配置一个嵌入式容器。 
- 自动配置数据库访问：Spring Boot 提供了多个数据库访问框架的 Starter 依赖项，如 Spring Data JPA、Spring Data MongoDB 和 Spring Data Cassandra。当你在 Spring Boot 应用程序中添加一个 Spring Boot Starter 依赖项时，Spring Boot 会自动配置一个数据库访问框架。 
- 自动配置安全性：Spring Boot 提供了多个安全性框架的 Starter 依赖项，如 Spring Security。当你在 Spring Boot 应用程序中添加一个 Spring Boot Starter 依赖项时，Spring Boot 会自动配置一个安全性框架。 
