
(unmaintainable code)[https://www.mindprod.com/jgloss/unmain.html]

> If builders built buildings the way programmers write programs, then the first woodpecker that came along would destroy civilization.
> 如果建筑商建造建筑的方式是程序员编写程序，那么第一个来到啄木鸟会毁灭文明。
> Gerald Weinberg (1933-10-27 age:84) Weinberg’s Second Law 杰拉尔德·温伯格 （1933-10-27，年龄：84 岁） 温伯格第二定律

> Never ascribe to malice, that which can be explained by incompetence.
> 永远不要归咎于恶意，那可以用无能来解释的。
> Napoléon Bonaparte 拿破仑·波拿巴

This essay has been like rock candy, seed the string with sugar, soak in sugar water, soon it grew out of control.

这篇文章就像糖果一样，先把糖撒在绳子上，浸泡在糖水中，很快它就失控了。

In the interests of creating employment opportunities in the Java programming field, I am passing on these tips from the masters on how to write code that is so difficult to maintain, that the people who come after you will take years to make even the simplest changes. Further, if you follow all these rules religiously, you will even guarantee yourself a lifetime of employment, since no one but you has a hope in hell of maintaining the code. Then again, if you followed all these rules religiously, even you wouldn’t be able to maintain the code!

为了在 Java 编程领域创造就业机会，我传授这些大师们的技巧，教你如何编写极其难以维护的代码，以至于后人即使是最简单的改动也要花费多年时间。此外，如果你严格遵守所有这些规则，你甚至能保证终身就业，因为没有人 但你几乎没有希望维护代码。不过，如果你跟着去的话 严格遵守这些规则 ，就算是你也维护不了代码！

You don’t want to overdo this. Your code should not look hopelessly unmaintainable, just be that way. Otherwise it stands the risk of being rewritten or refactored.

你不想做得太过头。你的代码不应该看起来 完全无法维护，就这样吧 。否则它就有被重写或重构的风险。

## Copyright  版权

I would like to remind you this essay is copyrighted material. It is illegal to repost it without permission. I will usually give you that permission if you translate the essay into another language and if you provide a link back to the English-language original. I do this for three reasons.

我想提醒你，这篇文章是受版权保护的材料。未经许可转载是违法的。如果你把文章翻译成另一种语言，并且提供英文原文的链接，我通常会批准你。我这样做有三个原因。

1. That way any change I make to the essay is instantly reflected in any English-language copy anyone reads.
    这样我对文章的任何修改都会立刻反映在任何英文文稿中。
2. That way the formatting and images are preserved. Pirated copies usually screw up the formatting.
    这样格式和图片就能被保留。盗版通常会搞乱格式。
3. Google ad revenue from this essay is the main source of income from the website. It pays to keep me on the air.
    谷歌广告收入是该网站的主要收入来源。让我继续直播是有好处的。

Please report any copyright violations.

请举报任何版权侵权行为。

## General Principles  一般原则

To foil the maintenance programmer, you have to understand how he thinks. He has your giant program. He has no time to read it all, much less understand it. He wants to rapidly find the place to make his change, make it and get out and have no unexpected side effects from the change.

要阻止维护程序员，你必须了解他的思维方式。他有你的庞大程序。他没有时间全部读完，更别说理解了。他想快速找到地方去改变，成功后离开，避免改变带来的意外副作用。

He views your code through a toilet paper tube. He can only see a tiny piece of your program at a time. You want to make sure he can never get at the big picture from doing that. You want to make it as hard as possible for him to find the code he is looking for. But even more important, you want to make it as awkward as possible for him to safely `ignore` anything.

他通过卫生纸管查看你的代码。他只能看到你一小部分 一次做项目。你要确保他永远无法通过行动触及大局 那个。你要让他尽可能难找到他想要的代码。 但更重要的是，你要让他尽可能尴尬，安全地 `什么都忽略` 。

Programmers are lulled into complacency by conventions. By every once in a while, by subtly violating convention, you force him to read every line of your code with a magnifying glass.

程序员被惯例所麻痹，陷入自满。偶尔，你通过微妙地打破惯例，强迫他用放大镜仔细阅读你的代码。

You might get the idea that every language feature makes code unmaintainable — not so, only if properly misused.

你可能会误以为每个语言特性都让代码无法维护——其实并非如此，只有被误用时才会如此。

Refactoring is a most emotionally-satisfying activity. It is second only to sex. Your own inchoate intention suddenly shines through with blinding clarity.

重构是一项极具情感满足感的活动。仅次于性。你那模糊的意图突然清晰地闪耀出来

## Naming  命名

> "When I use a word", Humpty Dumpty said, in a rather scornful tone, "It means just what I choose it to mean — neither more nor less."
> "当我用一个词时，" 哈姆蒂·丹普蒂用相当轻蔑的语气说， "它的意思正是我选择的——既不多也不少。"
> Lewis Carroll 刘易斯·卡罗尔

Much of the skill in writing unmaintainable code is the art of naming variables and methods. They don’t matter at all to the compiler. That gives you huge latitude to use them to befuddle the maintenance programmer.

写不可维护代码的技巧很大程度上在于变量和方法的命名艺术。它们对编译器来说根本无关紧要。这给了你很大的自由度去用它们来迷惑维护程序员。

### New Uses For Names For Baby 婴儿名字

Buy a copy of a baby naming book and you’ll never be at a loss for variable names. Fred is a wonderful name and easy to type. If you’re looking for easy-to-type variable names, try adsf or aoeu if you type with a DSK keyboard.

买一本婴儿命名书，你永远不会缺少变量名。Fred 是个很棒的名字，也很容易打字。如果你想找容易打字的变量名，可以试试 adsf，或者如果你用 DSK 键盘打字，可以试试 aoeu。

### Single Letter Variable Names 简化变量名称

: If you call your variables a, b, c, then it will be impossible to search for instances of them using a simple text editor. Further, nobody will be able to guess what they are for. If anyone even hints at breaking the tradition honoured since FØRTRAN of using i, j and k for indexing variables, namely replacing them with ii, jj and kk, warn them about what the Spanish Inquisition did to heretics.

如果你把变量命名为 a、b、c，那么用简单的文本编辑器就不可能搜索它们的实例。此外，没人能猜出它们的用途。如果有人稍微打破自 FØRTRAN 以来使用 i、j 和 k 作为变量索引的传统，即用 ii、jj 和 kk 替代，那就警告他们西班牙宗教裁判所对异端所做的事情。

### Creative Miss-spelling 创造性的拼写错误

: If you must use descriptive variable and function names, misspell them. By misspelling in some function and variable names and spelling it correctly in others (such as SetPintleOpening SetPintalClosing) we effectively negate the use of grep or IDE (Integrated Development Environment) search techniques. It works amazingly well. Add an international flavor by spelling tory or tori in different theatres/theaters.

：如果你必须使用描述变量和函数 名字，拼写错误。通过在某些函数和变量名称中拼写错误， 在其他（如 SetPintleOpening SetPintalClosing）中正确拼写时，我们 有效地消除了 grep 或 IDE 的使用（ 我已经 n negrate 了 D 扩展 E 环境 ） 搜索技巧。效果非常好。通过拼写增加国际风味 托里或托里在不同的剧院/剧院演出。

### Be Abstract 要抽象

: In naming functions and variables, make heavy use of abstract words like it, everything, data, handle, stuff, do, routine, perform and the digits e.g. routineX48, PerformDataFunction, DoIt, HandleStuff and do_args_method.

：在命名函数和变量时，大量使用抽象 像它这样的词， 一切 ， 数据 ， 掌控 ， 事情 、 做 、 例行、 执行以及数字，例如： routineX48， PerformDataFunction， 做、 处理事情和 do_args_method。

### A.C.R.O.N.Y.M.S. 使用缩写

Use acronyms to keep the code terse. Real men never define acronyms; they understand them genetically. If you confuse even yourself with acronymns, secretly write code using meaningful names, then, when the code in working, use the global rename feature of Eclipse to give your variables and methods unintelligible names and acronyms, just the way a mechanical obfuscator would.

用缩略词保持代码简洁。真正的男人从不定义 缩略词;他们从基因上理解它们。如果你甚至把自己搞混了 首字母词，秘密编写带有意义名称的代码，然后，当代码在 工作中，可以使用 Eclipse 的全局重命名功能来提供变量和方法 名字和缩写难以理解，就像机械混淆器一样。

### Thesaurus Surrogatisation 同义词

To break the boredom, use a thesaurus to look up as much alternate vocabulary as possible to refer to the same action, e.g. display, show, present. Vaguely hint there is some subtle difference, where none exists. However, if there are two similar functions that have a crucial difference, always use the same word in describing both functions (e.g. print to mean write to a file, put ink on paper and display on the screen).

为了打发无聊，可以用同义词词典查找 尽可能多地用替代词汇来指代同一动作，例如： 展示 、 展示 、 呈现 。隐约暗示有些微妙的 差异，即不存在的函数。然而，如果存在两个相似的函数，且 一个关键区别是，描述这两个功能时始终使用相同的词（例如： 打印意为写入文件 ， 将墨水涂在纸上并显示在屏幕上 ）。

### Eschew the Project Glossary 避免使用专业术语

Under no circumstances, succumb to demands to write a glossary with the special purpose project vocabulary unambiguously defined. Doing so would be an unprofessional breach of the structured design principle of information hiding. If you are forced to write such a vocabulary, use recursive definitions such as this one taken from the Ant 1.6.5 manual: basedir : the absolute path of the project’s basedir (as set with the basedir attribute of <project>). The reader still has no idea what the basedir is, though he has been given the clue it is absolute even though all examples show it with . (which, incidentally, is relative).

无论如何，都不要屈服于要求 编写一个明确定义特殊目的项目词汇表的词汇表。 这样做将违反结构化设计原则，不专业 信息隐藏 。如果你被迫写出这样的词汇表，可以使用递归定义，比如取自 Ant 1.6.5 手册的这个：basedir ： 项目 basedir 的绝对路径（以 basedir 属性<project> 设定）。 读者仍然不知道 basedir 是什么，尽管他已经获得了提示它是绝对的，尽管所有例子都显示它带有 。（其中， 顺便说一句，是相对的）。

Wear your adversary down by tantalising, pretending to give information where there is really none. Disguise your vacuous statements sufficiently so the reader will blame himself for failing to understand.

通过诱惑、假装提供信息来消耗对手，实际上并没有信息。掩饰你空洞的陈述，让读者自责没能理解。

The reader asks himself, if I am trying to compile the com.mindprod.holidays package, is basedir `C:\`, `C:\com\`, `J:\com\mindprod\` or `J:\com\mindprod\holidays\` ? You see why you should never use concrete examples? They are too clear. If you are forced to use them, complain that they sound childish and unprofessional. Complain that examples make it look as if that is all the product can do. You want people to appreciate fully every possible variation from the get go. You are not trying to inform, but impress! After all, no academic would be caught dead giving an example. People only respect that which is too abstract to grasp easily.

读者会问自己，如果我正在编译 com.mindprod.holidays 包，是基于 `C：\`、`C：\com\`、`J：\com\mindprod\` 或者 `J：\com\mindprod\holidays\` ? 你明白为什么绝不能用具体例子了吗？它们太清楚了。如果你是的话 被迫使用，抱怨它们听起来幼稚且不专业。抱怨 这些例子让人觉得这就是产品能做的全部 。你希望人们从一开始就充分欣赏每一个可能的变化。你不是想传递信息，而是想给人留下深刻印象！毕竟，没有学者会死都不会举例。人们只尊重那些过于抽象、难以理解的东西。

That’s a lot to remember. You will do just fine if all you do when writing documentation is maintain the attitude that people who don’t already know this jargon are stupid fools who don’t deserve to understand.

这需要记住很多东西。如果你在写文档时只是保持一种态度，认为那些不懂这些行话的人是愚蠢的傻瓜，不配理解，你会做得很好。

### Use Plural Forms From Other Languages 使用其他国家语言表达

A VAX/VMS (Virtual Address Extension/Virtual Memory System) script kept track of the statii returned from various Vaxen. Esperanto, Klingon qualifies as languages for these purposes. For pseudo-Esperanto pluraloj, add oj. You will be doing your part toward world peace.

VAX/VMS（VIRTUAL Address Extension/Virtual Memory System）脚本记录从各种数据返回的记录 瓦克森 。 世界语、 克林贡语在这些目的上属于语言。对于伪世界语复数语，可以加上 oj。你将为世界和平尽一份力。

### CapiTaliSaTion  失去语义简略

Randomly capitalize the first letter of a syllable in the middle of a word. For example: ComputeRasterHistoGram().

在 说到一半。例如：ComputeRasterHistoGram（）。

### Reuse Names  重复使用名称

Wherever the rules of the language permit, give classes, constructors, methods, member variables, parameters and local variables the same names. For extra points, reuse local variable names inside {} blocks. The goal is to force the maintenance programmer to carefully examine the scope of every instance. In particular, in Java, make ordinary methods masquerade as constructors.

在语言规则允许的地方，给出类， 构造函数、方法、成员变量、参数和局部变量相同 名字。为了增加点数，可以在 {} 块内重复使用局部变量名。目标是 强制维护程序员仔细检查每个实例的范围 。特别是在 Java 中，让普通方法伪装成构造函数。

### Åccented Letters  特殊字符

Use accented characters on variable names, e. g.

在变量名上使用带重音的字符，例如：

```c++
typedef struct { int i; } ínt;
```

where the second ínt’s í is actually i-acute. With only a simple text editor, it’s nearly impossible to distinguish the slant of the accent mark.

其中第二个 ínt 的 í 实际上是 i-急性。仅用简单的文本编辑器，几乎无法区分重音符号的倾斜。

### Exploit Compiler Name Length Limits 利用编译器名称长度限制

If the compiler will only distinguish the first, say, 8 characters of names, then vary the endings e.g. var_unit_update() in one case and var_unit_setup() in another. The compiler will treat both as var_unit.

如果编译器只会区分 首先，比如说，8个字符的名字，然后改变结尾，例如： 其中一种情况 var_unit_update（）， 另一种情况下 var_unit_setup（） 编译器会把两者都当作 var_unit。

### Underscore, a Friend Indeed 下划线

Use _ and __ as identifiers.

使用_和__作为标识符。

### Mix Languages  混合语言

Randomly intersperse two languages (human or computer). If your boss insists you use his language, tell him you can organise your thoughts better in your own language, or, if that does not work, allege linguistic discrimination and threaten to sue your employers for a vast sum.

随机分布两种语言（人类或计算机）。如果你的老板坚持让你用他的语言，告诉他你能更好地用自己的语言组织思路，或者如果不行，就指控语言歧视，并威胁要起诉雇主索赔巨额赔偿。

### Extended ASCII (American Standard Code for Information Interchange) 扩展 ASCII

Extended ASCII characters are perfectly valid as variable names, including ß, Ð and ñ characters. They are almost impossible to type without copying/pasting in a simple text editor.

扩展 ASCII 字符作为变量名完全有效，包括 ß、Ð 和 ñ 字符。几乎不可能不复制粘贴在简单的文本编辑器里打字。

### Names From Other Languages 其他语言命名

Use foreign language dictionaries as a source for variable names. For example, use the German punkt for point. Maintenance coders, without your firm grasp of German, will enjoy the multicultural experience of deciphering the meaning.

使用外语词典作为参考资料 变量名称。比如，用德国的 punkt 来表示点数。维护程序员如果没有扎实的德语掌握，也能享受多元文化的解读过程。

### Names From Mathematics  数学名称命名

Choose variable names that masquerade as mathematical operators, e.g.:

选择伪装成数学算符的变量名称，例如：

```shell
openParen = ( slash + asterix ) / equals;
```

### Bedazzling Names  哗众取宠又无关联的命名

Choose variable names with irrelevant emotional connotation, e. g.:

选择带有无关情感含义的变量名称，例如：

```shell
marypoppins = ( superman + starship ) / god;
```

This confuses the reader because they have difficulty disassociating the emotional connotations of the words from the logic they’re trying to think about.

这让读者感到困惑，因为他们很难将词语的情感含义与他们试图思考的逻辑区分开来。

### Rename and Reuse  更名与再利用

This trick works especially well in Ada, a language immune to many of the standard obfuscation techniques. The people who originally named all the objects and packages you use were morons. Rather than try to convince them to change, just use renames and subtypes to rename everything to names of your own devising. Make sure to leave a few references to the old names in, as a trap for the unwary.

这一技巧在 Ada 语言中尤其有效，Ada 语言对许多标准混淆技术免疫。最初给你用的所有对象和包命名的人都是蠢货。与其试图说服他们改名，不如用重命名和子类型把所有东西重命名成你自己想的名字。记得保留一些旧名字的引用，作为对不小心者的陷阱。

### When To Use i 何时使用 i

Never use `i` for the innermost loop variable. Use anything but. Use `i` liberally for any other purpose especially for non-int variables. Similarly use `n` as a loop index.

绝不要用 `i` 来表示最内层的环变量。别用任何东西。在其他用途上，尤其是非智数变量，请大量使用 `i`。同样，使用 `n` 作为循环索引。

### Lower Case l Looks a Lot Like the Digit 1 小写字母 l 看起来很像数字 1

Use lower case l to indicate long constants, e. g. 10l is more likely to be mistaken for 101 that 10L is. Ban any fonts that clearly disambiguate uvw wW gq9 2z 5s il17|!j oO08 `'" ;:,. m nn rn {[()]}. Be creative.

使用小写 l 表示长 常数，例如 10L 更容易被误认为 101，而 10L 是 10L。禁止任何字体 这明显是 UVW WW GQ9 2Z 5S IL17|！j oO08 ''“ ;：，.m nn rn {[（）]}。发挥创意。

### Reuse of Global Names as Private 全局名称作为私有名称的重用

Declare a global array in module A and a private one of the same name in the header file for module B, so that it appears that it’s the global array you are using in module B, but it isn’t. Make no reference in the comments to this duplication.

在模块 A 声明一个全局数组，在模块 B 的头文件中声明一个同名的私有数组，这样看起来就像你在模块 B 中使用的全局数组，但实际上不是。评论中请勿提及这种重复。

### Recycling Revisited  回收再访

Use scoping as confusingly as possible by recycling variable names in contradictory ways. For example, suppose you have global variables A and B and functions foo and bar. If you know that variable A will be regularly passed to foo and B to bar, make sure to define the functions as function foo(B) and function bar(A) so that inside the functions A will always be referred to as B and vice versa. With more functions and globals, you can create vast confusing webs of mutually contradictory uses of the same names.

通过以矛盾的方式重复变量名，尽可能让作用域变得混乱。例如，假设你有全局变量 A 和 B，以及函数 foo 和 bar。如果你知道变量 A 会定期传递给 foo，B 传递给 bar，确保定义函数为 foo（B）和 bar（A），这样在函数 A 内部总是被称为 B，反之亦然。有了更多的函数和全局函数，你可以制造出大量相互矛盾的名称使用网络。

### Recycle Your Variables  回收你的变量

Wherever scope rules permit, reuse existing unrelated variable names. Similarly, use the same temporary variable for two unrelated purposes (purporting to save stack slots). For a fiendish variant, morph the variable, for example, assign a value to a variable at the top of a very long method and then somewhere in the middle, change the meaning of the variable in a subtle way, such as converting it from a 0-based coordinate to a 1-based coordinate. Be certain not to document this change in meaning.

在作用域规则允许的情况下，重复使用现有的无关变量名称。同样，将同一个临时变量用于两个无关的目的（声称节省堆栈槽位）。对于一个复杂的变体，比如变形变量，给一个非常长的方法顶部的变量赋值，然后在中间某个地方，以微妙的方式改变变量的含义，比如把它从基于0的坐标转换到基于1的坐标。务必不要记录这种意义的变化。

### Misleading names  误导性名称

Make sure that every method does a little bit more (or less) than its name suggests. As a simple example, a method named isValid(x) should as a side effect convert x to binary and store the result in a database.

确保每种方法都能做得稍微多一点（或少一点） 比名字所暗示的要多。举个简单的例子，一个名为 isValid（x） 的方法应作为副作用将 x 转换为二进制，并将结果存储在数据库中。

### m_

a naming convention from the world of C++ is the use of m_ in front of members. This is supposed to help you tell them apart from methods, so long as you forget that method also starts with the letter m.

C++ 世界中的一个命名惯例是在成员前使用 m_。这本应帮助你区分它们和方法，只要你忘了方法也是以字母开头的 M。

### o_apple obj_apple

Use an o or obj prefix for each instance of the class to show that you’re thinking of the big, polymorphic picture.

每个实例都用 o 或 obj 前缀，表明你在思考一个宏观的多态图景。

### Obscure film references  鲜为人知的电影引用

Use constant names like `LancelotsFavouriteColour` instead of `blue` and assign it hex value of 0x0204FB. The color looks identical to pure blue on the screen and a maintenance programmer would have to work out 0204FB (or use some graphic tool) to know what it looks like. Only someone intimately familiar with Monty Python and the Holy Grail would know that Lancelot’s favorite color was blue. If a maintenance programmer can’t quote entire Monty Python movies from memory, he or she has no business being a programmer.

使用恒定名称，比如 `LancelotsFavouriteColour` 代替`蓝色` ，并赋值 它的十六进制值是 0x0204FB。屏幕上的颜色看起来和纯蓝色一样，而且 维护程序员需要计算 0204FB（或使用图形工具）来实现 知道它长什么样。只有对蒙提·派森和 圣杯会知道兰斯洛特最喜欢的颜色是蓝色。如果是维护 程序员无法凭记忆背诵整部蒙提·派森电影，但他或她能 不该当程序员。
