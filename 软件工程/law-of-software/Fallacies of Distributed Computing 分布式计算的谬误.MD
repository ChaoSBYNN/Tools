# Fallacies of Distributed Computing 分布式计算的谬误

> 分布式系统设计者常犯的八个错误。

* 网络会丢弃消息、引入延迟、吞吐量有限且可能不安全。正确构建的分布式系统必须通过重试、超时、安全措施和动态发现来考虑这些因素。
* 这些谬误常常表现为细微的漏洞。假设延迟为零可能导致远程通话变得冗长，本地运行良好，但在网络上却变得异常缓慢。
* 考虑到这些谬误会导致防御性设计：使用缓存（带宽/延迟不完美）、构建冗余（网络不可靠）、处理动态成员关系（拓扑变化）。

## Overview  概述

八大谬误作为一份不可轻易假设的清单。它们都源于将分布式系统视为本地系统。开发者可能会写代码，仿佛调用远程服务就像调用本地函数一样，忽略了延迟和故障。

这些错误假设导致严重问题：网络崩溃时出现未处理错误、因忽视延迟导致性能差、因未能认证远程通话而导致的安全漏洞。

通过称之为“谬误”，创作者试图灌输一种心态：网络会失败，行为不理想，所以你的系统必须设计成容忍这种现象。

## Examples  示例

开发者构建分布式缓存系统时假设“网络延迟为零”。他们设计缓存以每次查找时从远端节点获取数据。实际上，它表现抖动，延迟高且性能不佳。

系统假设“网络是安全的”，发送敏感数据时不加密，或者不验证来自其他服务的输入。如果网络被攻破，这会导致数据泄露。没有为拓扑结构的改变做准备，导致系统在意外添加或移除时出现故障。

## Origins  起源

主要归功于 L. Peter Deutsch（还有 James Gosling 等人），大约在 1994 年于 Sun Microsystems 出版。最初存在七个谬误后来高斯林又增加了第八个（“网络是同质的”）。

Deutsch 观察到，许多工程师，尤其是习惯本地计算的工程师，会潜意识地认为网络就是能正常运作的。将这些假设形式化为列表，帮助团队记得要处理每一项。

## Fallacies  谬误

### The network is reliable 网络可靠

Software applications are written with little error-handling on networking errors. During a network outage, such applications may stall or infinitely wait for an answer packet, permanently consuming memory or other resources. When the failed network becomes available, those applications may also fail to retry any stalled operations or require a (manual) restart.

软件应用程序在编写时几乎没有对网络错误的错误处理。在网络中断期间，这些应用程序可能会卡顿或无限等待应答包，永久消耗内存或其他资源。当故障网络可用时，这些应用程序也可能无法重试任何停顿的操作，或需要（手动）重启。

### Latency is zero 延迟为零

Ignorance of network latency, and of the packet loss it can cause, induces application- and transport-layer developers to allow unbounded traffic, greatly increasing dropped packets and wasting bandwidth.

对网络延迟及其可能导致丢包的无知，促使应用层和传输层开发者允许无界流量，极大增加丢包并浪费带宽。

### Bandwidth is infinite 带宽是无限的

Ignorance of bandwidth limits on the part of traffic senders can result in bottlenecks.

流量发送方对带宽限制的无知可能导致瓶颈。

### The network is secure 网络是安全的

Complacency regarding network security results in being blindsided by malicious users and programs that continually adapt to security measures.

对网络安全的自满会导致被恶意用户和不断适应安全措施的程序措手不及。

### Topology doesn't change 拓扑结构不会改变

Changes in network topology can have effects on both bandwidth and latency issues, and therefore can have similar problems.

网络拓扑的变化会影响带宽和延迟问题，因此也可能出现类似的问题。

### There is one administrator 有一位管理员

Multiple administrators, as with subnets for rival companies, may institute conflicting policies of which senders of network traffic must be aware in order to complete their desired paths.

多个管理员，如同竞争对手公司的子网 ，可能会制定相互冲突的策略，网络流量发送方必须了解这些策略才能完成其期望路径。

### Transport cost is zero 运输成本为零

The "hidden" costs of building and maintaining a network or subnet are non-negligible and must consequently be noted in budgets to avoid vast shortfalls.

构建和维护网络或子网的“隐性”成本不可忽视，因此必须在预算中注明以避免巨额赤字。

### The network is homogeneous 网络是同质性的

If a system assumes a homogeneous network, then it can lead to the same problems that result from the first three fallacies.

如果一个系统假设网络是同质的，那么它可能导致前三个谬误所带来的同样问题。
