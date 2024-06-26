# Clean Architecture

uncle Bob文章以及翻译：http://www.cnblogs.com/wanpengcoder/p/3479322.html

> Clean Code 的作者 Robert C. Martin (Uncle Bob)，写了一本书 Clean Architecture，从而提出了这个架构 (Clean Coder Blog)

Bob提取出来大部分架构所需要的准则：

- 框架独立。架构不依赖于一些满载功能的软件库。
- 可测试性。
- UI独立，在不改变系统其余部分的情况下完成对UI的简易更改。
- 数据库独立，业务规则不绑定与某个具体的数据库当中，可以随意更换数据库的具体实现：比如说从SQL换到BigTable，这种情况不会对业务规则产生影响。
- 外部机制独立，业务规则完全不知道外层的事情。

![Clean](https://pic1.zhimg.com/80/v2-28f5a77fe859101974d013bbd6cf61f8_720w.webp)

`图中的总体思想是依赖的方向只能是从外向内。也就是说，外部的圈依赖内部的圈，内部的圈不能依赖外部。也就是说内圈对外圈一无所知，没有任何依赖。外层中命名和数据格式不能影响内层。`

根据这些共有的理念，bob尝试将它们整合到一个单一可执行的想法中。这就是clean架构，而图片中的同心圆是架构思想的体现，代码用一种依赖规则分离到洋葱状的层：内层不应该知道关于外层的东西，依赖应该从外到内。

## What Clean Architecture 分层

### Entities

封装业务规则。 这一层的组成可以是包含方法的对象，也可以是数据结构和方法。这一层封装了最通用和高层级的业务规则。 和业务规则不相关的改变不应该影响到这一层。 【在Clean Architecture的原文中对于这一层的描述是封装了企业级别的业务规则，这一层的实体可以被企业内的多个Application引用；如果没有企业级的业务规则，那这一层就封装哪些最通用和最高层的业务规则，这些业务规则不会轻易发生变化】

### Use Cases

这一层封装应用级别的业务规则，这一层实现了系统的use cases。这一层主要是实现对Entities层的编排，通过调用更底层的Entities层的对象来实现一个用例。

这一层的改变不会影响到Entities层。同时，外部的改变，比如数据库/UI的变更也不会影响这一层。 只有这个应用的用例的改变才会引起这一层发生变化。

### Interface Adapters

数据转换的地方(DataMapper) 主要用来做Use Cases/Entities层 和 外部系统（展示层，数据库层）之间的数据格式的适配。

### Frameworks and Drivers

这一层是最外面的框架层，比如数据库/web 框架等。

### 只能是4层？

这个架构图里有4层，但是这个只是一个示意图，不一定非得有4层。具体有几层不是最重要的，最重要的是依赖顺序要保证是从外到内的。 也就是说，越往内层，逻辑的抽象层次越高。越往内层，抽象的层次越高，封装的逻辑越高级，越通用。

![Layer](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2017/9/27/cf6d29b80b62da4905155c1cbe5fff5e~tplv-t2oaga2asx-jj-mark:3024:0:0:0:q75.png)

### Presentation Layer 表现层

也就是MVX结构所对应的地方（MVC、MVP等），这里不处理UI以外的任何逻辑。

![Presentation](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2017/9/27/ccccf90377a0baa49d63141cdd1a487f~tplv-t2oaga2asx-jj-mark:3024:0:0:0:q75.png)

P层使得V层（Fragment和Activity）内部除UI逻辑再无其它逻辑。而我的开源项目中的Presenter由多个Interactor组成。

### Domain Layer 领域层

业务逻辑,use case实现的地方，在这里包含了use case(用例)以及Bussiness Objects(业务对象),按照洋葱图的依赖规则，这层属于最内层，也就是完全不依赖于外层。

![Domain](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2017/9/27/1950f115073f471ca4b9beb4bc4f88cc~tplv-t2oaga2asx-jj-mark:3024:0:0:0:q75.png)

interactor的实现类和业务对象。

### Data Layer 数据层

![Data](https://p1-jj.byteimg.com/tos-cn-i-t2oaga2asx/gold-user-assets/2017/9/27/797ee7d22b857162e258f0432d996c08~tplv-t2oaga2asx-jj-mark:3024:0:0:0:q75.png)

这是一种Repository模式

所有APP需要的数据都是通过这层的XXDataRepository(实现了Domain层接口的类)提供的，它使用了Repository Pattern，关于Repository Pattern你可以参考我的翻译文章

简要概括就是使用Repository将业务层与具体的查询逻辑分离，Repository的实现类主要职责就是存储查询数据，一个更简单的解释就是你可以把Repository的实现类当成是一个Java容器(Collections)，可以从中获取或存储数据(add/remove/etc),他对数据源进行了抽象。Domain层提供接口而不关心Data层到底是如何实现的，Data层的Repository只需要实现相关接口提供相关服务

## How 控制流程

图的右下角是代码控制流的走向。是 按照`Controller -> Use Cases -> Presenter`这个路径走的。这里面有两个向右的箭头，表示的是代码的依赖方向。表示Controller和Presenter都依赖User Case层的代码。 你会发现一个问题，就是实际的代码运行的控制流程和代码的依赖方向是冲突的，这里就是通过接口实现了依赖反转。 图中的<I>表示接口，空三角箭头代表实现了接口，实线的箭头表示实际的依赖/调用关系。 举个例子，右侧的Use Case层的Use Case Interactor需要调用外层的Presenter，但是不能直接调用，因为这违反了依赖关系。所以，在内层(Use Case层) 加了一个接口Use Case Output Port，Use Case Interactor直接调用这个接口，而外层的Presenter实现了这个接口，这里就是依赖反转。

![Flow](https://pic2.zhimg.com/80/v2-207d045077f56c6e4c6f6ee3dba9c59d_720w.webp)

### 穿越边界的数据传递

外层和内层之间进行数据传递时，可以用简单的数据类型进行传递，或者用DTO进行传递。但是有一个原则就是不能违反外层只依赖内层的这个原则。 所以说，外层在传递参数给内层的时候，尽量要保证参数的独立，简单，最小化原则。不能把外层自己的非常复杂的数据结构不加修改的直接传递给内层，这样就会导致内层需要理解外层的数据结构。这就违反了依赖原则。正确的做法应该是外层传递参数给内层的时候，只传内层需要的参数。

