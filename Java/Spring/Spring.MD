# Spring 是什么

Spring 是 Java EE 编程领域的一款轻量级的开源框架，由被称为“Spring 之父”的 Rod Johnson 于 2002 年提出并创立，它的目标就是要简化 Java 企业级应用程序的开发难度和周期。

Spring 自诞生以来备受青睐，一直被广大开发人员作为 Java 企业级应用程序开发的首选。时至今日，Spring 俨然成为了 Java EE 代名词，成为了构建 Java EE 应用的事实标准。

## Spring 的诞生与发展

早期的 J2EE（Java EE 平台）推崇以 EJB 为核心的开发方式，但这种开发方式在实际的开发过程中存在种种弊端，例如使用复杂、代码臃肿、代码侵入性强、开发周期长、移植难度大等。

Rod Johnson 在其 2004 年编著的畅销书《Expert One-on-One J2EE Development without EJB》中，针对 EJB 各种臃肿的结构进行了逐一的分析和否定，并分别以更加简洁的方式进行了替换。

在这本书中，Rod Johnson 通过一个包含 3 万行代码的附件，展示了如何在不使用 EJB 的情况下构建一个高质量、可扩展的 Java 应用程序。在这个附件中，Rod Johnson 编写了上万行基础结构代码，其中包含了许多可重用的 Java 接口和类，例如 ApplicationContext、BeanFactory 等。这些类的根包被命名为 com.interface21，含义为：这是提供给 21 世纪的一个参考。

这本书影响甚远，后来 Rod Johnson 将 com.interface21 的代码开源，并把这个新框架并命名为“Spring”，含义为：Spring 像一缕春风一样，扫平传统 J2EE 的寒冬。

- 2003 年 2 月，Spring 0.9 版本发布，它采用了 Apache 2.0 开源协议；
- 2004 年 4 月，Spring 1.0 版本正式发布。
- 到目前为止，Spring 已经步入到了第 5 个大版本，也就是我们常说的 Spring 5。

## Spring 的狭义和广义

在不同的语境中，Spring 所代表的含义是不同的。下面我们就分别从“广义”和“狭义”两个角度，对 Spring 进行介绍。

### 广义的 Spring：Spring 技术栈

广义上的 Spring 泛指以 Spring Framework 为核心的 Spring 技术栈。

经过十多年的发展，Spring 已经不再是一个单纯的应用框架，而是逐渐发展成为一个由多个不同子项目（模块）组成的成熟技术，例如 Spring Framework、Spring MVC、SpringBoot、Spring Cloud、Spring Data、Spring Security 等，其中 Spring Framework 是其他子项目的基础。

这些子项目涵盖了从企业级应用开发到云计算等各方面的内容，能够帮助开发人员解决软件发展过程中不断产生的各种实际问题，给开发人员带来了更好的开发体验。

|项目名称|描述|
|---|---|
|Spring Data|Spring 提供的数据访问模块，对 JDBC 和 ORM 提供了很好的支持。通过它，开发人员可以使用一种相对统一的方式，来访问位于不同类型数据库中的数据。|
|Spring Batch|一款专门针对企业级系统中的日常批处理任务的轻量级框架，能够帮助开发人员方便的开发出健壮、高效的批处理应用程序。|
|Spring Security|前身为 Acegi，是 Spring 中较成熟的子模块之一。它是一款可以定制化的身份验证和访问控制框架。|
|Spring Mobile|是对 Spring MVC 的扩展，用来简化移动端 Web 应用的开发。|
|Spring Boot|是 Spring 团队提供的全新框架，它为 Spring 以及第三方库一些开箱即用的配置，可以简化 Spring 应用的搭建及开发过程。|
|Spring Cloud|一款基于 Spring Boot 实现的微服务框架。它并不是某一门技术，而是一系列微服务解决方案或框架的有序集合。它将市面上成熟的、经过验证的微服务框架整合起来，并通过 Spring Boot 的思想进行再封装，屏蔽调其中复杂的配置和实现原理，最终为开发人员提供了一套简单易懂、易部署和易维护的分布式系统开发工具包。|

### 狭义的 Spring：Spring Framework

狭义的 Spring 特指 Spring Framework，通常我们将它称为 Spring 框架。

Spring 框架是一个分层的、面向切面的 Java 应用程序的一站式轻量级解决方案，它是 Spring 技术栈的核心和基础，是为了解决企业级应用开发的复杂性而创建的。

Spring 有两个核心部分： IoC 和 AOP。

|核心|描述|
|---|---|
|IoC|Inverse of Control 的简写，译为“控制反转”，指把创建对象过程交给 Spring 进行管理。|
|AOP|Aspect Oriented Programming 的简写，译为“面向切面编程”。</br> AOP 用来封装多个类的公共行为，将那些与业务无关，却为业务模块所共同调用的逻辑封装起来，减少系统的重复代码，降低模块间的耦合度。另外，AOP 还解决一些系统层面上的问题，比如日志、事务、权限等。|

## Spring Framework 的特点

1. 方便解耦，简化开发 : Spring 就是一个大工厂，可以将所有对象的创建和依赖关系的维护交给 Spring 管理。
2. 方便集成各种优秀框架 : Spring 不排斥各种优秀的开源框架，其内部提供了对各种优秀框架（如 Struts2、Hibernate、MyBatis 等）的直接支持。
3. 降低 Java EE API 的使用难度 : Spring 对 Java EE 开发中非常难用的一些 API（JDBC、JavaMail、远程调用等）都提供了封装，使这些 API 应用的难度大大降低。
4. 方便程序的测试 : Spring 支持 JUnit4，可以通过注解方便地测试 Spring 程序。
5. AOP 编程的支持 : Spring 提供面向切面编程，可以方便地实现对程序进行权限拦截和运行监控等功能。
6. 声明式事务的支持 : 只需要通过配置就可以完成对事务的管理，而无须手动编程。
