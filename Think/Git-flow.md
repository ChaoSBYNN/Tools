---
title: Git flow
date: 2017-04-15 22:58:27
tags: Git
---

### Production|Master 分支 ： 合并读取
这个分支最近发布到生产环境的代码，最近发布的Release， 这个分支只能从其他分支合并，不能在这个分支直接修改

### Develop 分支 ： 开发
这个分支是我们是我们的主开发分支，包含所有要发布到下一个Release的代码，这个主要合并与其他分支，比如Feature分支

### Feature 分支 ： 相互独立
这个分支主要是用来开发一个新的功能，一旦开发完成，我们合并回Develop分支进入下一个Release

### Release分支 ： 需求冻结
当你需要一个发布一个新Release的时候，我们基于Develop分支创建一个Release分支，完成Release后，我们合并到Master和Develop分支

### Hotfix分支 ： 紧急修复
当我们在Production发现新的Bug时候，我们需要创建一个Hotfix, 完成Hotfix后，我们合并回Master和Develop分支，所以Hotfix的改动会进入下一个Release

![](/images/2017_04_15_0.png)