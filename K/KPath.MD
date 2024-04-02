```
└─com
    └─k
        ├─boss -- 项目/软件/服务的简称
        │  ├─demo
        │  │  ├─api
        │  │  ├─application
        │  │  └─infrastructure
        │  └─manager -- 模块名称，严格按照功能划分，请确保模块之间业务耦合性低
        │      ├─api -- 接口层，放API、Controller，及一般只有Controller会使用的Query和VO（View Object），这些对象应该有完善的Swagger注解做标记。
        │      ├─application -- 应用层，放一般业务，及程序内部使用的DTO。注：禁止在Service里套娃DAO、Repository等
        │      │  ├─command -- 涉及到写数据的业务，不做读写分离时请忽略
        │      │  └─query -- 只读数据业务，不做读写分离时请忽略
        │      ├─domain -- 领域/实体层
        │      │  ├─event -- 事件
        │      │  └─repository -- 存储，通过repository来访问mapper、redis等。
        │      └─infrastructure -- 基础设施，不写业务
        │          ├─exception -- 异常，所有不需要程序处理的异常都抛出
        │          └─persistence -- 持久化
        │              ├─mapper -- mybatis
        │              └─redis
        ├─common
        │  ├─error
        │  ├─model
        │  ├─persistence
        │  │  └─mybatis
        │  ├─result
        │  └─web
        │      └─version
        └─util -- 工具
```
