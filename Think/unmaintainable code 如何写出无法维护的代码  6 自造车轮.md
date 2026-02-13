## Roll Your Own  自造车轮

> Definition of Upward Compatible 向上兼容的定义
> I’ve finally learned what upward compatible means. It means we get to keep all our old mistakes.
> 我终于明白了什么是向上兼容 。这意味着我们可以保留所有旧错误。
> Dennie van Tassel 丹尼·范·塔塞尔

You’ve always wanted to write system level code. Now is your chance. Ignore the standard libraries and write your own. It will look great on your resumé.

你一直想写系统级代码。现在是你的机会。忽略标准库，自己写一个。这会在你的简历上加分。

### Roll Your Own BNF (Backus-Naur Form) 自己制作 BNF（Backus-N aur Form）

: Always document your command syntax with your own, unique, undocumented brand of BNF notation. Never explain the syntax by providing a suite of annotated sample valid and invalid commands. That would demonstrate a complete lack of academic rigour. Railway diagrams are almost as gauche. Make sure there is no obvious way of telling a terminal symbol (something you would actually type) from an intermediate one — something that represents a phrase in the syntax. Never use typeface, colour, caps, or any other visual clues to help the reader distinguish the two. Use the exact same punctuation glyphs in your BNF notation that you use in the command language itself, so the reader can never tell if a (…), […], {…} or … is something you actually type as part of the command, or is intended to give clues about which syntax elements are obligatory, repeatable or optional in your BNF notation. After all, if they are too stupid to figure out your variant of BNF, they have no business using your program.

：始终用你的命令语法记录你的 这是独一无二、未公开的 BNF 符号体系。切勿通过提供一套带注释的示例有效和无效命令来解释语法。那将显示出完全缺乏学术严谨性。铁路图纸几乎同样粗糙。确保没有明显的方法能区分终结符号（你实际会打出的符号）和中间符号——即语法中代表短语的符号。切勿使用字体、颜色、大写或其他视觉线索帮助读者区分两者。在你的 BNF 符号中使用与命令语言中完全相同的标点符号，这样读者永远无法判断（...）、[...]、[...}或 ...... 是你实际输入的命令内容，还是用来提示 BNF 符号中哪些语法元素是强制、可重复还是可选的。毕竟，如果他们笨到连你的 BNF 变体都搞不懂，他们根本不该用你的程序。

### Roll Your Own Allocator 自己制作分配器

: Everyone knows that debugging your dynamic storage is complicated and time consuming. Instead of making sure each class has no storage leaks, reinvent your own storage allocator. It just mallocs space out of a big arena. Instead of freeing storage, force your users to periodically perform a system reset that clears the heap. There’s only a few things the system needs to keep track of across resets — lots easier than plugging all the storage leaks; and so long as the users remember to periodically reset the system, they’ll never run out of heap space. Imagine them trying to change this strategy once deployed!

： 众所周知，调试动态存储既复杂又耗时。与其确保每个类别没有存储泄漏，不如重新设计你自己的存储分配器。它只是从大场馆中挤出空间。不要释放存储空间，而是强制用户定期执行系统重置以清除堆。系统只需要在重置期间跟踪几件事——这比堵塞所有存储泄漏要简单得多;只要用户记得定期重置系统，堆空间就永远不会用完。想象一下，一旦部署，他们还试图改变这个策略！
