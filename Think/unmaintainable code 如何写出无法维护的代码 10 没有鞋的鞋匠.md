## The Shoemaker Has No Shoes 没有鞋的鞋匠

> Three Laws Of Prediction 预测三大定律
> 1. When a distinguished but elderly scientist states that something is possible, he is almost certainly right.
>   当一位杰出但年迈的科学家说某件事是可能的时，他几乎肯定是对的。
> 2. When he states that something is impossible, he is very probably wrong. The only way of discovering the limits of the possible is to venture a little way past them into the impossible.
>   当他说某事不可能时，他很可能错了。发现可能性极限的唯一途径，就是稍微越过它们，进入不可能的领域。
> 3. Any sufficiently advanced technology is indistinguishable from magic.
>   任何足够先进的技术都与魔法无异。
> Arthur C. Clarke 亚瑟·C·克拉克

Imagine having an accountant as a client who insisted on maintaining his general ledgers using a word processor. You would do you best to persuade him that his data should be structured. He needs validation with cross field checks. You would persuade him he could do so much more with that data when stored in a database, including controlled simultaneous update.

想象一下，有个会计师作为客户，坚持用文字处理器维护他的总账。你会尽力说服他，他的数据应该是结构化的。他需要通过交叉现场的检查来验证。你会说服他，如果数据存储在数据库中，他可以做更多事情，包括可控的同时更新。

Imagine taking on a software developer as a client. He insists on maintaining all his data (source code) with a text editor. He is not yet even exploiting the word processor’s colour, type size or fonts.

想象一下，接手一位软件开发人员作为客户。他坚持用文本编辑器维护所有数据（源代码）。他甚至还没开始利用文字处理器的颜色、字号或字体。

Think of what might happen if we started storing source code as structured data. We could view the same source code in many alternate ways, e.g. as Java, as NextRex, as a decision table, as a flow chart, as a loop structure skeleton (with the detail stripped off), as Java with various levels of detail or comments removed, as Java with highlights on the variables and method invocations of current interest, or as Java with generated comments about argument names and/or types. We could display complex arithmetic expressions in 2D, the way TeX and mathematicians do. You could see code with additional or fewer parentheses, ( depending on how comfortable you feel with the precedence rules ). Parenthesis nests could use varying size and colour to help matching by eye. With changes as transparent overlay sets that you can optionally remove or apply, you could watch in real time as other programmers on your team, working in a different country, modified code in classes that you were working on too.

想想如果我们开始把源代码作为结构化数据存储，会发生什么。我们可以以多种不同方式看待同一源代码，例如 Java、NextRex、决策表、流程图、环路结构骨架（去除细节）、去除不同细节层级或注释的 Java、在当前感兴趣的变量和方法调用上标注的 Java， 或者以 Java 形式生成参数名称和/或类型注释。我们可以像 TeX 和数学家那样，在二维中展示复杂的算术表达式。你可能会看到代码中括号多或少，（ 取决于你对优先规则的熟悉程度 ）。括号巢可以用不同大小和颜色来帮助眼睛匹配。通过透明叠加集的更改，你可以选择移除或应用，实时观察团队中其他程序员在不同国家工作时，修改你正在参与的课程代码。

You could use the full colour abilities of the modern screen to give subliminal clues, e.g. by automatically assigning a portion of the spectrum to each package/class using a pastel shades as the backgrounds to any references to methods or variables of that class. You could bold face the definition of any identifier to make it stand out.

你可以利用现代屏幕的全彩色功能来提供潜意识线索，比如用粉彩色阴影自动为每个包/类分配一部分光谱，作为该类方法或变量的引用背景。你可以用粗体字标记任何标识符的定义来突出。

You could ask what methods/constructors will produce an object of type X? What methods will accept an object of type X as a parameter? What variables are accessible in this point in the code? By clicking on a method invocation or variable reference, you could see its definition, helping sort out which version of a given method will actually be invoked. You could ask to globally visit all references to a given method or variable, and tick them off once each was dealt with. You could do quite a bit of code writing by point and click.

你可以问，哪些方法/构造器会生成类型为 X 的对象？哪些方法会接受类型为 X 的对象作为参数？在代码中这个阶段可以使用哪些变量？点击方法调用或变量引用，可以看到其定义，帮助判断实际调用的方法是哪个版本。你可以请求全局访问某个方法或变量的所有引用，处理完后再勾选。你可以用点选写很多代码。

Some of these ideas would not pan out. But the best way to find out which would be valuable in practice is to try them. Once we had the basic tool, we could experiment with hundreds of similar ideas to make life easier for the maintenance programmer.

其中一些想法未能实现。但了解哪些在实际作中有用，最好的方法是亲自尝试。有了基础工具后，我们可以尝试数百种类似的想法，让维护程序员的工作更轻松。

I discuss this further in the SCID (Source Code In Database) student project.

我在 SCID（Source Code In Database） 学生项目中进一步讨论了这个问题。

An early version of this article appeared in Java Developers’ Journal (volume 2 issue 6). I also spoke on this topic in 1997-11 at the Colorado Summit Conference. It has been gradually growing ever since. I have had quite a few requests for permission to build links here. You are welcome to create links, but please don’t repost the essay since the original changes frequently.

本文的早期版本曾发表于 Java 版本 开发者杂志（第 2 卷第 6 期）。我也在 1997-11 年科罗拉多峰会会议上就此主题发表过演讲。此后该项目逐渐增长。我收到了不少关于在这里建立链接许可的请求。你可以创建链接，但请不要重新发布文章，因为原始内容经常更改。

If you enjoyed this essay you might like this one on how to write like a newbie. There is a ton of stuff on this site quite unlike anything else on the web. Have a quick look at my home page or my Java Glossary which is a central place to find out everything you ever wanted to know about Java or perhaps my Gay & Black Glossary. If you want a bird’s eye view of all the things I’m involved in, see the CMP_home home page.

如果你喜欢这篇文章，你可能会喜欢这篇关于如何像新手一样写作的文章。这个网站上有大量与网络上其他东西截然不同的内容。快速浏览我的主页或 Java 词汇表 ，那里是了解你想知道的一切关于 Java 或我的同性恋与黑人词汇表的核心平台。如果你想从鸟瞰视角了解我参与的所有事情，请查看 CMP_home  主页 。

You might also enjoy the famous essays Worse Is Better and The Rise of Worse Is Better on doing the right thing.

你可能还会喜欢著名的文章《 坏即是好 》和《 坏即是好 》这两篇关于做正确事情的文章。
