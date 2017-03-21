---
title: 数据挖掘-Spark
date: 2017-03-21 22:12:43
tags: [DataMining,Tools,Spark]
---

> Spark : 借鉴了MapReduce之上发展而来的，继承了其分布式并行计算的优点并改进了MapReduce明显的缺陷

# 运行模式

|环境|描述|模式|
|:---:|:---:|:---:|
|**local**|本地单进程模式|本地模式|
|**standalone**|分布式集群,Master-Worker架构（或者Master-Slave），<br>Master负责调度，Worker负责具体Task的执行|集群模式|
|**on yarn/mesos**|通过YARN或Mesos作为资源管理，Spark作为调度控制|集群模式|
|**on cloud**|AWS（Amazom Web Services）的EC2（Elastic Compute Cloud）<br>服务|集群模式|


# Spark生态系统

![](/images/2017_03_21_1.bmp)

1. Spark SQL : 允许直接处理RDD，也可查询Hive、HBase等外部数据源。
2. Spark Streaming ： 实时数据流处理组件（Storm）。
3. MLlib : 机器学习算法，含聚类、分类、回归、协同过滤。
4. GraphX : 用于图计算API。
4. Spark Core : Spark的基本功能，内存计算、任务调度、部署模式、故障恢复、存储管理等
5. Standalone Scheduler : 分布式独立运行。
6. YARN : Apache Hadoop YARN （Yet Another Resource Negotiator，另一种资源协调者）是一种新的 Hadoop 资源管理器。
7. Mesos : Mesos是Apache下的开源分布式资源管理框架，它被称为是分布式系统的内核。


# 基本概念

|概念|描述|
|:---:|:---:|
|RDDs|Resillient Distributed DataSets，弹性分布式数据集，是分布式内存的一个抽象概念，提供一种高度受限的共享内存模型|
|DAG|Directed Acyclic Graph，有向无环图，反映RDD之间的依赖关系|
|Application|Spark的应用程序，包含一个Driver program和若干Executor|
|Driver Program|运行Application的main()函数并且创建SparkContext|
|Cluster Manager|在集群上获取资源的外部服务(例如：Standalone、Mesos、Yarn)|
|Worker Node|集群中任何可以运行Application代码的节点，运行一个或多个Executor进程|
|Executor|是为Application运行在工作节点（Worker Node）上的一个进程，该进程负责运行Task，并且负责将数据存在内存或者磁盘上。每个Application都会申请各自的Executor来处理任务|
|SparkContext|Spark应用程序的入口，负责调度各个运算资源，协调各个Worker Node上的Executor|
|Job|一个Job含多个RDD及作用于RDD上的各种操作，SparkContext提交的具体Action操作，常和Action对应|
|Stage|是Job的基本调度单位，一个Job分为多组Task，每组Task被称为Stage，或者称为TaskSet，代表了一组关联的、相互之间没有Shuffle依赖关系的任务组成的任务集|
|Task|运行在Executor上的工作单元|
|DAGScheduler|根据Job构建基于Stage的DAG，并提交Stage给TaskScheduler|
|TaskScheduler|将Taskset提交给Worker node集群运行并返回结果|
|Transformations|是Spark API的一种类型，Transformation返回值还是一个RDD，所有的Transformation采用的都是懒策略，如果只是将Transformation提交是不会执行计算的|
|Action|是Spark API的一种类型，Action返回值不是一个RDD，而是一个scala集合；计算只有在Action被提交的时候计算才被触发|

# Spark运行流程

1. 提交一个Application后，首先构建基本运行环境，即由Driver创建一个SparkContext，由SparkContext负责和资源管理器（Cluster Manager）的通信，以及资源的申请、任务的分配和监控等。SparkContext会向资源管理器注册并申请运行Executor的资源；
2. 资源管理器为Executor分配资源，启动Executor进程，Executor运行情况随着“心跳”发送到资源管理器上；
3. SparkContext根据RDD的依赖关系构建DAG图，DAG图提交给DAGScheduler进行解析，将DAG图分解成Stage，并计算Stage间的依赖关系，然后将一个个TaskSet（即Stage）提交给底层调度器TaskScheduler进行处理；Executor向SparkContext申请Task，TaskScheduler将Task发放给Executor运行，同时，SparkContext将应用程序代码发送给Executor；
4. Task在Executor上运行，结果反馈给TaskScheduler，然后反馈给DAGScheduler，运行完毕后写入数据，并释放资源。

# Spark架构特点

* 每个Application都有专属的Executor进程，并且在Application运行期间一直驻留。Executor进程以多线程的方式运行Task；
* Spark运行过程与资源管理器无关，只要能获取Executor进程并保存通信即可；
* Task采用数据本地性和推测执行等优化机制。数据本地性，即计算向数据靠拢，移动计算比移动数据占得网络资源要少。Spark采用延时调度机制，可在更大程度上实现执行过程优化。

