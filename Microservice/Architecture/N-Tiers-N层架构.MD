# N-Tiers N层架构

> 是相对于两层结构而言的。传统的C/S 计算多是基于两级模式 ，在这种模式中，所有的形式逻辑和业务逻辑均驻留在Client 端，而Server则成为数据库 服务器，负责各种数据的处理和维护。因此Server变得很“瘦”，被称为“瘦服务器 (Thin Server)”。与之相反，这种模式需要在客户端 运行庞大的应用程序 ，这就是所谓的“胖客户机 (Fat Client)”。

这种体系结构将应用程序组织成水平层，每个层负责特定的任务。

N层架构 （n-tiers 结构，多层式运行架构）的四层是指Presentation Tier（表示层，就是直接呈现在用户面前的界面）、Web Server Tier（Web服务器层）、 Application Server Tier（应用服务器层）和 Data Tier（数据层）.

早期的网络系统设计常常采用三层结构。最常见的结构，就是表示(presentation)层 , 领域(domain)层 , 以及基础架构(infrastructure)层。

- 表现层
- 业务逻辑
- 数据存取

![n-tiers](https://pic3.zhimg.com/80/v2-b5839c53d542d7de46c2a3da5d9e61ae_720w.webp)

J2EE 规范提出了自己的n-tiers结构。下表是几种模型的对照:

|ISA|Brown|J2EE|对应程序部分|
|:---|:---|:---|:---|
|表示层|表示层|客户层|终端|
|||服务器表示层|JSP,ActionFrom|
||控制/中介层||Controller,Action|
|领域层|领域层|业务层|JavaBean,SessionBean|
||数据映射层|整合层|EntityBean,JDO,JDBC|
|数据层|数据源层|资源层|RDBMS数据库, Redis|
