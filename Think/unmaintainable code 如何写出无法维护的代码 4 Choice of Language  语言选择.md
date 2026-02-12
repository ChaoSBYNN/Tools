## Choice of Language  语言选择

> Philosophy is a battle against the bewitchment of our intelligence by means of language.
> 哲学是一场对抗语言对我们智慧迷惑的斗争。
> Ludwig Wittgenstein 路德维希·维特根斯坦

Computer languages are gradually evolving to become more fool proof. Using state of the art languages is unmanly. Insist on using the oldest language you can get away with, octal machine language if you can (Like Hans und Frans, I am no girlie man; I am so virile I used to code by plugging gold tipped wires into a plugboard of IBM (International Business Machines) unit record equipment (punch cards), or by poking holes in paper tape with a hand punch), failing that assembler, failing that FØRTRAN or COBOL, failing that C and BASIC, failing that C++.

计算机语言正在逐步发展，变得更加万无一失。使用状态 艺术语言不够男子气概。坚持使用你能逃避的古老语言， 如果可以的话，八进制机器语言（像汉斯和弗朗斯一样，我不是女孩子;我很喜欢 我以前用金色尖线插在插座板上编程 IBM（I nternational Business Machines）单位记录设备（穿孔卡），或者用手打孔器在纸带上戳孔），如果该汇编器失败，失败该 FØRTRAN 或 COBOL，失败 C 和 BASIC，失败 C++。

### FØRTRAN

Write all your code in FØRTRAN. If your boss ask why, you can reply that there are lots of very useful libraries that you can use thus saving time. However, the chances of writing maintainable code in FØRTRAN are zero and therefore following the unmaintainable coding guidelines is a lot easier.

把所有代码都用 FØRTRAN 写。如果你的老板问为什么，你可以回答说有很多非常有用的库可以用，这样可以节省时间。然而，在 FØRTRAN 中编写可维护代码的可能性为零，因此遵循难以维护的编码指南要容易得多。

### Avoid Ada  避开Ada

About 20% of these techniques can’t be used in Ada. Refuse to use Ada. If your manager presses you, insist that no-one else uses it and point out that it doesn’t work with your large suite of tools like lint and plummer that work around C’s failings.

大约 20% 的这些技术在Ada语中无法使用。拒绝使用 Ada。如果你的经理催促你，坚持别人不用它，并指出它不适合你那套绕过 C 缺陷的大量工具，比如绒毛和管路器。

### Use ASM (Assembler)

Convert all common utility functions into asm.

将所有常见的效用函数转换为 asm。

### Use QBASIC

Leave all important library functions written in QBASIC, then just write an asm wrapper to handle the large->medium memory model mapping.

所有重要的库函数都用 QBASIC 写，然后写一个 ASM 包装器来处理大>中型内存模型映射。

### Inline Assembler  在线汇编器

Sprinkle your code with bits of inline assembler just for fun. Almost no one understands assembler anymore. Even a few lines of it can stop a maintenance programmer cold.

在代码里加点内联汇编工具，纯粹是个乐趣。几乎没人再懂汇编器了。哪怕只有几行，也能让维护程序员彻底停工。

### MASM (`M`icrosoft `As`se`m`bler) call C

If you have assembler modules which are called from C, try to call C back from the assembler as often as possible, even if it’s only for a trivial purpose and make sure you make full use of the goto, bcc and other charming obfuscations of assembler.

如果你有汇编模块需要从 C 调用，尽量多从汇编器回调用 C，即使只是出于琐碎的目的，也要充分利用汇编器的 goto、BCC 和其他迷人的混淆功能。

### Regex  正则表达式

Regexes are notoriously hard to proofread and debug. Use them copiously, the longer and more convoluted the better.

正则表达式以校对和调试难度著称。多用它们，越长越复杂越好。

### Avoid Maintainability Tools 避免使用可维护性工具

: Avoid coding in Abundance, or using any of its principles kludged into other languages. It was designed from the ground up with the primary goal of making the maintenance programmer’s job easier. Similarly avoid Eiffel or Ada since they were designed to catch bugs before a program goes into production.

避免在 《丰盛 》中编码，或使用其任何被拼凑到其他语言中的原则。它从零开始设计 ，主要目标是让维护程序员的工作更轻松。同样，避免使用 Eiffel 或 Ada，因为它们设计用于在程序投入生产前捕捉漏洞。
