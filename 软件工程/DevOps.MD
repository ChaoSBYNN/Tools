# DevOps

[DevOps到底是什么意思？](https://zhuanlan.zhihu.com/p/91371659)

## DevOps的起源

这个故事有点长，从头开始讲起吧。

上个世纪40年代，世界上第一台计算机诞生。从诞生之日起，它就离不开程序（Program）的驱动。而负责编写程序的人，就被称为“程序员”（Programmer）。

![origin](https://pic1.zhimg.com/80/v2-854a121a745e6fb7d7e38c9a24015d38_720w.webp)

程序员是计算机的驾驭者，也是极其稀缺的人才。那个时候，只有高学历、名校出身的人，才有资格成为程序员，操控计算机。

随着人类科技的不断发展，PC和Internet陆续问世，我们进入了全民拥抱信息化的时代。越来越多的企业开始将计算机作为办公用的工具，用以提升生产力。而普通个人用户也开始将计算机作为娱乐工具，用以改善生活品质。

于是，计算机的程序，开始变成了一门生意。程序，逐步演进为“软件（software）”，变成了最赚钱的产品之一。

在软件产业里，程序员有了更专业的称谓，叫做“软件开发工程师（Software Development Engineer）”，也就是我们常说的“码农”。

我们知道，一个软件从零开始到最终交付，大概包括以下几个阶段：规划、编码、构建、测试、发布、部署和维护。

![flow](https://pic2.zhimg.com/80/v2-ea189369b289d1c8aa80aaacf209e169_720w.webp)

最初，程序比较简单，工作量不大，程序员一个人可以完成所有阶段的工作。

随着软件产业的日益发展壮大，软件的规模也在逐渐变得庞大。软件的复杂度不断攀升。一个人已经hold不住了，就开始出现了精细化分工。

码农的队伍扩大，工种增加。除了软件开发工程师之外，又有了软件测试工程师，软件运维工程师。

![flow2](https://pic3.zhimg.com/80/v2-bfc28ea6167dc53d2a49f938dec328aa_720w.webp)

分工之后，传统的软件开发流程是这样的：

软件开发人员花费数周和数月编写代码，然后将代码交给QA（质量保障）团队进行测试，然后将最终的发布版交给运维团队去布署。所有的这三个阶段，即开发，测试，布署。

早期所采用的软件交付模型，称之为“瀑布（Waterfall）模型”。

![waterfall](https://pic3.zhimg.com/80/v2-b6666853138a7c4fe3f93abe65f49942_720w.webp)

瀑布模型，简而言之，就是等一个阶段所有工作完成之后，再进入下一个阶段。

这种模型适合条件比较理想化（用户需求非常明确、开发时间非常充足）的项目。大家按部就班，轮流执行自己的职责即可。

但是，项目不可能是单向运作的。客户也是有需求的。产品也是会有问题的，需要改进的。

![stage](https://pic2.zhimg.com/80/v2-7716da4551b1125368bcb384e5ce08b1_720w.webp)

随着时间推移，用户对系统的需求不断增加，与此同时，用户给的时间周期却越来越少。在这个情况下，大家发现，笨重迟缓的瀑布式开发已经不合时宜了。

于是，软件开发团队引入了一个新的概念，那就是大名鼎鼎的——“敏捷开发（Agile Development）”。

敏捷开发在2000年左右开始被世人所关注，是一种能应对快速变化需求的软件开发能力。其实简单来说，就是把大项目变成小项目，把大时间点变成小时间点，然后这样：

有两个词经常会伴随着DevOps出现，那就是CI和CD。CI是Continuous Integration（持续集成），而CD对应多个英文，Continuous Delivery（持续交付）或Continuous Deployment（持续部署）。

美其名曰：“持续（Continuous）”，其实就是“加速——反复——加速——反复……”，这样子。

画个图大家可能更明白一点：

![flow3](https://pic3.zhimg.com/80/v2-e8ed10c2f4a348547012026e8f18182e_720w.webp)

敏捷开发大幅提高了开发团队的工作效率，让版本的更新速度变得更快。

很多人可能会觉得，“更新版本的速度快了，风险不是更大了吗？”

其实，事实并非如此。

敏捷开发可以帮助更快地发现问题，产品被更快地交付到用户手中，团队可以更快地得到用户的反馈，从而进行更快地响应。而且，DevOps小步快跑的形式带来的版本变化是比较小的，风险会更小（如下图所示）。即使出现问题，修复起来也会相对容易一些。

![compare](https://pic3.zhimg.com/80/v2-d41f952cd0d67499d288c9abd4936296_720w.webp)

虽然敏捷开发大幅提升了软件开发的效率和版本更新的速度，但是它的效果仅限于开发环节。研发们发现，运维那边，依旧是铁板一块，成为了新的瓶颈。

运维工程师，和开发工程师有着完全不同的思维逻辑。运维团队的座右铭，很简单，就是“稳定压倒一切”。运维的核心诉求，就是不出问题。

什么情况下最容易出问题？发生改变的时候最容易出问题。所以说，运维非常排斥“改变”。

于是乎，矛盾就在两者之间集中爆发了。

这个时候，我们的DevOps，隆重登场了。

## DevOps到底是什么

DevOps这个词，其实就是Development和Operations两个词的组合。它的英文发音是 /de'vɒps/，类似于“迪沃普斯”。

![devops](https://pic4.zhimg.com/80/v2-958480d0c41c3bda19d85d6a7eeaa46f_720w.webp)

DevOps的维基百科定义是这样的：

DevOps是一组过程、方法与系统的统称，用于促进开发、技术运营和质量保障（QA）部门之间的沟通、协作与整合。

![Dev&QA&Ops](https://pic1.zhimg.com/80/v2-fd762b5d94ed770082618be93b8647f8_720w.webp)

这个定位稍微有点抽象，但是并不难理解。反正它不是某一个特定软件、工具或平台的名字。

从目标来看，DevOps就是让开发人员和运维人员更好地沟通合作，通过自动化流程来使得软件整体过程更加快捷和可靠。

![break](https://pic1.zhimg.com/80/v2-db505c761dcbebf41286e1a0e7e2f718_720w.webp)

很多人可能觉得，所谓DevOps，不就是Dev+Ops嘛，把两个团队合并，或者将运维划归开发，不就完事了嘛，简单粗暴。

注意，这个观点是不对的。这也是DevOps这些年一直难以落地的主要原因。

想要将DevOps真正落地，首先第一点，是思维转变，也就是“洗脑”。不仅是运维的要洗，开发的也要洗。员工要洗，领导更要洗。

DevOps并不仅仅是组织架构变革，更是企业文化和思想观念的变革。如果不能改变观念，即使将员工放在一起，也不会产生火花。

除了洗脑之外，就是根据DevOps思想重新梳理全流程的规范和标准。

在DevOps的流程下，运维人员会在项目开发期间就介入到开发过程中，了解开发人员使用的系统架构和技术路线，从而制定适当的运维方案。而开发人员也会在运维的初期参与到系统部署中，并提供系统部署的优化建议。

DevOps的实施，促进开发和运维人员的沟通，增进彼此的理（gan）解（qing）。

在思维和流程改变的同时，想要充分落地DevOps，当然离不开软件和平台的支持。

换言之，DevOps考验的不仅是一家企业的技术，更是管理水平和企业文化。

对比前面所说的瀑布式开发和敏捷开发，我们可以明显看出，DevOps贯穿了软件全生命周期，而不仅限于开发阶段。

![flow4](https://pic1.zhimg.com/80/v2-b132353da6b138a6bf53ae3bb4d43020_720w.webp)

下面这张图，更明显地说明了DevOps所处的位置，还有它的价值：

![flow5](https://pic4.zhimg.com/80/v2-5008335da1745de0a3188f960894480b_720w.webp)

## DevOps的发展现状

