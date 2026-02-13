## Miscellaneous Techniques  其他技术

> We should forget about small efficiencies, say about 97% of the time; premature optimization is the root of all evil.
> 我们应该忘记那些小的效率，比如大约 97% 的时间;过早优化是万恶之源。
> Donald Ervin Knuth 唐纳德·欧文·克努斯

> If you give someone a program, you will frustrate them for a day; if you teach them how to program, you will frustrate them for a lifetime.
> 如果你给别人一个程序，你会让他们沮丧一天;如果你教他们编程，你会让他们一辈子都感到沮丧。
> Anonymous

### Don’t Recompile  不要重新编译

: Let’s start off with probably the most fiendish technique ever devised: Compile the code to an executable. If it works, then just make one or two small little changes in the source code…in each module. But don’t bother recompiling these. You can do that later when you have more time and when there’s time for debugging. When the hapless maintenance programmer years later makes a change and the code no longer works, she will erroneously assume it must be something she recently changed. You will send her off on a wild goose chase that will keep her busy for weeks.

：我们先从可能最邪恶的开始 最有说服力的技术：将代码编译成可执行文件。如果有效，那就 对源代码做一两个小改动......在每个模块中。 但别费心重新编译这些。 你可以等有更多时间、有时间调试时再做。多年后，当那位倒霉的维护程序员做了修改，代码不再正常工作时，她会错误地认为那一定是她最近改过的东西。你会让她白费力气，忙上好几个星期。

### S.I. vs American Measure S.I.与美国衡量法

: In engineering work there are two ways to code. One is to convert all inputs to S.I. (metric) units of measure, then do your calculations then convert back to various civil units of measure for output. The other is to maintain the various mixed measure systems throughout. Always choose the second. It’s the American way!

在工程工作中，编码有两种方式。一种是将所有输入转换为 S.I.（公制）计量单位，然后进行计算，再将输出换回各种民用计量单位。另一种是贯穿全程保持各种混合度量系统。永远选择第二个。这就是美国的方式！

### CANI (Constant And Never-ending Improvement) 常量和不断改进 

: Make improvements to your code often and force users to upgrade often - after all, no one wants to be running an outdated version. Just because they think they’re happy with the program as it is, just think how much happier they will be after you’ve fixed it! Don’t tell anyone what the differences between versions are unless you are forced to — after all, why tell someone about bugs in the old version they might never have noticed otherwise?

： 经常改进代码，强制用户频繁升级——毕竟没人想运行过时版本。仅仅因为他们觉得对现有项目满意，想想看你修好后他们会更开心！除非被迫，否则别告诉任何人版本之间的差异——毕竟，为什么要告诉别人旧版本里的漏洞，否则他们可能根本不会注意到呢？

### About Box  关于 Box

: The About Box should contain only the name of the program, the names of the coders and a copyright notice written in legalese. Ideally it should link to several megs of code that produce an entertaining animated display. However, it should never contain a description of what the program is for, its minor version number, or the date of the most recent code revision, or the website where to get the updates, or the author’s email address. This way all the users will soon all be running on different versions and will attempt to install version N+2 before installing version N+1.

： 关于方框应仅包含程序名称， 程序员姓名以及用法律术语书写的版权声明。理想情况下应该是这样 链接到几 MB 代码，生成有趣的动画展示。然而， 绝不应包含程序用途的描述、次要版本号、最近一次代码修订日期、更新网站或作者电子邮件地址。这样所有用户很快就会在不同版本上运行，并会先安装版本 N+2，再安装版本 N+1。

### Ch ch ch Changes 编辑更改

: The more changes you can make between versions the better, you don’t want users to become bored with the same old API (Application Programming Interface) or user interface year after year. Finally, if you can make this change without the users noticing, this is better still — it will keep them on their toes and keep them from becoming complacent.

：你能在不同版本之间做的改动越多越好， 你不希望用户对老一套感到厌倦 API（应用程序 P 表格式） 或用户界面年复一年。最后，如果你能在用户不注意的情况下完成这一改变，那就更好了——这会让他们保持警觉，避免自满。

### Put C Prototypes In Individual Files 将 C 原型放入单独文件中

: instead of common headers. This has the dual advantage of requiring a change in parameter data type to be maintained in every file, and avoids any chance that the compiler or linker will detect type mismatches. This will be especially helpful when porting from 32 -> 64-bit platforms.

： 而不是共用的头部。这 需要参数数据类型更改以维护的双重优势 每个文件， 并避免编译器或链接器 检测类型不匹配。这在从 32 转到 3% E 时尤其有用 64 位平台。

### No Skill Required  无需技巧

: You don’t need great skill to write unmaintainable code. Just leap in and start coding. Keep in mind that management still measures productivity in lines of code even if you have to delete most of it later.

写不可维护的代码不需要高超的技能。直接跳进去开始写代码。请记住，管理层仍然以代码行数来衡量生产力，即使你后来得删掉大部分代码。

### Carry Only One Hammer 只携带一把锤子

: Stick with what you know and travel light; if you only carry a hammer then all problems are nails.

：坚持你所知道的，轻装上阵;如果你只带锤子，那么所有问题都是钉子。

### Standards Schmandards 

: Whenever possible ignore the coding standards currently in use by thousands of developers in your project’s target language and environment. For example insist on STL (Standard Template Library) style coding standards when writing an MFC (Microsoft Foundation Classes) based application.

：尽可能忽略编码标准 目前有成千上万的开发者在你项目的目标语言中使用 以及环境。例如，在编写基于 MFC（Microsoft Foundation Classes） 的应用程序时，坚持采用 STL（Standard Template Library）风格的编码标准。

### Reverse the Usual True False Convention 逆转通常的真假约定

: Reverse the usual definitions of true and false. Sounds very obvious but it works great. You can hide:

：颠倒对真与假的常规定义。听起来很明显，但效果很好。你可以隐藏：

```c
#define TRUE 0
#define FALSE 1
```

somewhere deep in the code so that it is dredged up from the bowels of the program from some file that no one ever looks at anymore. Then force the program to do comparisons like:

在代码的某个深处，从程序深处从某个没人再看的文件里挖掘出来。然后强制程序进行如下比较：

```c
if ( var == TRUE )
```

```c
if ( var != FALSE )
```

someone is bound to correct the apparent redundancy and use var elsewhere in the usual way:

总有人会纠正表面上的错误 冗余并以常规方式在其他地方使用 var：

```c
if ( var )
```

Another technique is to make TRUE and FALSE have the same value, though most would consider that out and out cheating. Using values 1 and 2 or -1 and 0 is a more subtle way to trip people up and still look respectable. You can use this same technique in Java by defining a static constant called TRUE. Programmers might be more suspicious you are up to no good since there is a built-in literal true in Java.

另一种方法是让真和假值相同，尽管大多数人会认为这完全是作弊。使用数值 1 和 2，或者-1 和 0，是更微妙的方式让人困惑，同时又能显得体面。你也可以在 Java 中使用同样的技术，定义一个叫 TRUE 的静态常量。程序员可能会怀疑你在搞鬼，因为 Java 里自带的字面意义 。

### Third Party Libraries  第三方库

: Include powerful third party libraries in your project and then don’t use them. With practice you can remain completely ignorant of good tools and add the unused tools to your resumé in your "Other Tools" section.

：在你的项目中加入强大的第三方库，然后不要使用它们。通过练习，你可以完全无知地了解好工具，把未用的工具添加到简历中的“其他工具”栏目。

### Avoid Libraries 避免使用库

: Feign ignorance of libraries that are directly included with your development tool. If coding in Visual C++ ignore the presence of MFC or the STL and code all character strings and arrays by hand; this helps keep your pointer skills sharp and it automatically foils any attempts to extend the code.

：对直接包含的库装作无知 用你的开发工具。如果用 Visual C++ 编码，忽略 MFC 或 STL 的存在，手动编写所有字符串和数组;这有助于保持指针技能的敏锐，也能自动阻止任何扩展代码的尝试。

### Create a Build Order 创建构建顺序

: Make it so elaborate that no maintainer could ever get any of his or her fixes to compile. Keep secret SmartJ which renders make scripts almost obsolete. Similarly, keep secret that the javac.exe compiler is also available as a class. On pain of death, never reveal how easy it is to write and maintain a speedy little custom java program to find the files and do the make that directly invokes the sun.tools.javac.Main compile class.

：做得如此复杂，以至于任何维护者都无法理解 他或她的任何修正要汇编。保密的 SmartJ，它几乎让脚本变得过时。同样，也要保密 javac.exe 编译器也作为类可用。否则，千万别透露编写和维护一个快速定制的 Java 程序有多容易，可以找到文件并完成直接调用 sun.tools.javac.Main 编译类的 Create。

### More Fun With Make 用 Make 更有趣

: Have the makefile-generated-batch-file copy source files from multiple directories with undocumented overrwrite rules. This permits code branching without the need for any fancy source code control system and stops your successors ever finding out which version of DoUsefulWork() is the one they should edit.

：让 makefile 生成批处理文件从多个目录复制源文件，并使用未文档覆盖的规则。这允许代码分支，无需复杂的源代码控制系统，也防止继任者发现该编辑哪个版本的 DoUsefulWork（） 版本。

### Collect Coding Standards 收集编码标准

: Find all the tips you can on writing maintainable code such as the Square Box Suggestions and flagrantly violate them.

：找到所有写作技巧，方便管理 比如方形盒子建议 ，并公然违反这些建议。

### IDE (`I`ntegrated `D`evelopment `E`nvironment), Not Me!

: Put all the code in the makefile: your successors will be really impressed how you managed to write a makefile which generates a batch file that generates some header files and then builds the app, such that they can never tell what effects a change will have, or be able to migrate to a modern IDE. For maximum effect use an obsolete make tool, such as an early brain dead version of NMAKE without the notion of dependencies.

：把所有代码放进 makefile 里：你的 继任者会非常惊讶你写出了一个临时文件 生成一个批处理文件，生成一些头文件，然后构建应用，例如 他们永远无法预知变化会产生什么影响，也无法迁移到 现代 IDE。为了达到最大效果，可以使用过时的制作工具，比如早期的脑残版本 NMAKE，没有依赖的概念。

### Bypassing Company Coding Standards 绕过公司编码标准

: Some companies have a strict policy of no numeric literals; you must use named constants. It is fairly easy to foil the intent of this policy. For example, one clever C++ programmer wrote:

：有些公司有严格的政策 无数字文字;你必须使用命名常量。要破坏 本政策的意图。例如，一个聪明的 C++ 程序员写道：

```c++
#define K_ONE 1
#define K_TWO 2
#define K_THOUSAND 999
```

### Compiler Warnings  编译器警告

: Be sure to leave in some compiler warnings. Use the handy - prefix in make to suppress the failure of the make due to any and all compiler errors. This way, if a maintenance programmer carelessly inserts an error into your source code, the make tool will nonetheless try to rebuild the entire package; it might even succeed! And any programmer who compiles your code by hand will think that they have broken some existing code or header when all that has really happened is that they have stumbled across your harmless warnings. They will again be grateful to you for the enjoyment of the process that they will have to follow to find out that the error was there all along. Extra bonus points: make sure that your program cannot possibly compile with any of the compiler error checking diagnostics enabled. Sure, the compiler may be able to do subscripts bounds checking, but real programmers don’t use this feature and neither should you. Why let the compiler check for errors when you can use your own lucrative and rewarding time to find these subtle bugs?

请务必保留一些编译员的警告。用手巧的 - 在 make 中加前缀，以抑制因编译器错误导致 make 失败的情况。这样，如果维护程序员不小心在源代码中插入错误，make 工具仍会尝试重建整个软件包;甚至可能成功！任何手动编译你的代码的程序员都会以为自己破坏了现有代码或头部，实际上只是偶然发现了你的无害警告。他们会再次感激你享受他们必须经历的过程，直到发现错误一直存在。额外提醒：确保你的程序不可能在启用编译器错误检查诊断的情况下编译。当然，编译器可能能做下标边界检查，但真正的程序员不会用这个功能，你也不应该用。为什么要让编译器检查错误，不如利用自己丰厚且有成就感的时间去发现这些细微的漏洞呢？

### Combine Bug Fixes With Upgrades 将修复漏洞与升级结合起来

: Never put out a bug fix only release. Be sure to combine bug fixes with database format changes, complex user interface changes and complete rewrites of the administration interfaces. That way, it will be so hard to upgrade that people will get used to the bugs and start calling them features. And the people that really want these features to work differently will have an incentive to upgrade to the new versions. This will save you maintenance work in the long run and get you more revenue from your customers.

：绝不发布只发布修复漏洞 。务必将错误修复与数据库格式调整结合起来， 复杂的用户界面变更和管理系统的全面重写 接口。这样升级会非常困难，人们会习惯 bug，然后开始称它们为功能。而真正想要这些的人 功能变化将激励升级到新版本。这从长远来看可以帮你节省维护工作，并为你带来更多客户收入。

### Change File Formats With Each Release of Your Product 每次产品发布时更改文件格式

: Yeah, your customers will demand upwards compatibility, so go ahead and do that. But make sure that there is no backwards compatibility. That will prevent customers from backing out the newer release and coupled with a sensible bug fix policy (see above), will guarantee that once on a newer release, they will stay there. Extra bonus points: Figure out how to get the old version to not even recognise files created by the newer versions. That way, they not only can’t read them, they will deny that they are even created by the same application! Hint: PC (Personal Computer) word processors provide a useful example of this sophisticated behaviour.

：是的，你的客户 这会要求向上兼容，所以去做吧。但一定要确保那里 没有向下兼容。这样可以防止客户放弃新款 发布并配合合理的漏洞修复政策（见上文），将保证 一旦在新版本中，它们就会一直保持在那个位置。额外加分：想办法 让旧版本甚至无法识别新版本创建的文件。那个 而且，他们不仅看不懂这些，还会否认这些书是被创造出来的 用的是同一个应用！提示：PC（ 物理 C 代词器）文字处理器提供了这种复杂行为的一个有用示例。

### Compensate For Bugs  补偿漏洞

: Don’t worry about finding the root cause of bugs in the code. Simply put in compensating code in the higher-level routines. This is a great intellectual exercise, akin to 3D chess and will keep future code maintainers entertained for hours as they try to figure out whether the problem is in the low-level routines that generate the data or in the high-level routines that change various cases all around. This technique is great for compilers, which are inherently multi-pass programs. You can completely avoid fixing problems in the early passes by simply making the later passes more complicated. With luck, you will never have to speak to the little snot who supposedly maintains the front-end of the compiler. Extra bonus points: make sure the back-end breaks if the front-end ever generates the correct data.

：不用担心找出代码中漏洞的根源。简单来说，是在高层例程中放入补偿代码。这是一个极好的智力练习，类似于三维国际象棋，未来的代码维护者会花上数小时娱乐性地思考问题出在生成数据的低层例程，还是在改变各种情况的高层例程中。这种技术非常适合编译器，编译器本质上是多重程序。你完全可以通过让后期的流程更复杂来避免早期路径的问题。运气好的话，你永远不用和那个据说维护编译器前端的小屁孩说话。额外加分：如果前端生成正确数据，确保后端会出问题。

### Use Spin Locks  使用旋转锁

: Avoid actual synchronization primitives in favor of a variety of spin locks — repeatedly sleep then test a (non-volatile) global variable until it meets your criterion. Spin locks are much easier to use and more general and flexible than the system objects.

：避免实际同步原语，转而使用 各种自旋锁——反复睡眠然后测试一个（非易失性）全局 变量直到达到你的标准。旋转锁用起来更简单，功能也更多 比系统对象更通用且灵活 。

### Sprinkle sync code liberally 大量散布同步代码

: Sprinkle some system synchronization primitives in places where they are not needed. I came across one critical section in a section of code where there was no possibility of a second thread. I challenged the original developer and he indicated that it helped document that the code was, well, critical!

：加点系统同步 在不需要的地方使用原始元素。我遇到一个关键的代码区，那里根本没有第二个线程。我质疑了最初的开发者，他表示这有助于证明代码是，嗯， 关键的！

### Graceful Degradation  优雅的降级

: If your system includes an NT device driver, require the application to malloc I/O buffers and lock them in memory for the duration of any transactions and free/unlock them after. This will result in an application that crashes NT if prematurely terminated with that buffer locked. But nobody at the client site likely will be able to change the device driver, so they won’t have a choice.

： 如果你的系统包含 NT 设备驱动程序，要求应用程序对 Mloc I/O 缓冲区进行锁定，并在交易期间将其锁定内存，之后释放或解锁。如果提前终止且该缓冲区被锁定，应用程序会导致该应用崩溃。但客户端的人很可能无法更改设备驱动，所以他们别无选择。

### Custom Script Language  自定义脚本语言

: Incorporate a scripting command language into your client/server apps that is byte compiled at runtime.

：在客户端/服务器应用中集成脚本命令语言，并在运行时进行字节编译。

### Compiler Dependent Code  编译器相关代码

: If you discover a bug in your compiler or interpreter, be sure to make that behaviour essential for your code to work properly. After all you don’t use another compiler and neither should anyone else!

：如果你发现编译器或解释器有漏洞，务必使该行为成为代码正常工作的关键。毕竟你不需要用其他编译器，别人也不需要！

### A Real Life Example 一个真实案例

: Here’s a real life example written by a master. Let’s look at all the different techniques he packed into this single C function.

这里有一个由大师写的真实案例。让我们看看他把所有不同的技术都塞进了这个单一的 C 函数里。

```c
void* Realocate(void*buf, int os, int ns)
void* Realocate（void*buf， int os， int ns）
{
void*temp;  
temp = malloc(os);  
memcpy((void*)temp, (void*)buf, os);  
free(buf);  
buf = malloc(ns);  
memset(buf, 0, ns);  
memcpy((void*)buf, (void*)temp, ns);  
return buf;  
}  
```

* Reinvent simple functions which are part of the standard libraries.
* 重新发明标准库中的简单函数。
* The word Realocate is not spelled correctly. Never underestimate the power of creative spelling.
* “Realocate”这个词拼写不正确。永远不要低估创意拼写的力量。
* Presume malloc will always return successfully.
* 假设马洛克总会成功归来。
* Make a temporary copy of input buffer for no real reason.
* 临时复制输入缓冲区，没有什么特别的原因。
* Cast things for no reason. memcpy() takes (void*), so cast our pointers even though they’re already (void*). Bonus for the fact that you could pass anything anyway.
* 无缘无故施放。memcpy（） 接受 （void*），所以即使指针已经是 （void*），也要施放它们。额外福利是你本来就能通过任何考试。
* Never bothered to free temp. This will cause a slow memory leak, that may not show up until the program has been running for days.
* 从没费心过自由温度。这会导致内存泄漏缓慢，可能要等程序运行几天后才会显现出来。
* Copy more than necessary from the buffer just in case. This will only cause a core dump on Unix, not Windows.
* 从缓冲区复制的量多一些，以防万一。这只会导致 Unix 上的核心数据导出，Windows 上不会。
* It should be obvious that os and ns stand for old size and new size.
* 很明显，os 和 ns 分别代表旧尺寸和新规模 。
* After allocating buf, memset it to 0. Don’t use calloc() because somebody might rewrite the ANSI (American National Standards Institute) spec so that calloc() fills the buffer with something other than 0. (Never mind the fact that we’re about to copy exactly the same amount of data into buf.)
* 分配完 buf，memset 它为 0。不要用 calloc（），因为有人可能会重写 ANSI（American N S STandards Institute）规范，使 calloc（） 填充缓冲区时除了 0 以外的东西。（别管我们即将复制完全相同数量的数据到 buf。）

### How To Fix Unused Variable Errors 如何修复未使用的变量错误

: If your compiler issues unused local variable warnings, don’t get rid of the variable. Instead, just find a clever way to use it. My favorite is…

： 如果你的编译器发出未使用的本地变量警告，不要删除该变量。相反，你只要找到一个巧妙的使用方式。我最喜欢的是......

```shell
i = i;
```

### It’s The Size That Counts 关键是大小

: It almost goes without saying that the larger a function is, the better it is. And the more jumps and GOTOs the better. That way, any change must be analysed through many scenarios. It snarls the maintenance programmer in the spaghettiness of it all. And if the function is truly gargantuan, it becomes the Godzilla of the maintenance programmers, stomping them mercilessly to the ground before they have an idea of what’s happened.

： 几乎不用说，函数越大，效果越好。跳跃和 GOTO 越多越好。这样，任何变化都必须通过多种情景进行分析。这让维护程序员感到愤怒，这种复杂的感觉令人难堪。如果这个功能真的非常庞大，它就会变成维护程序员的哥斯拉，在他们还没弄清楚发生了什么之前，就无情地将他们踩在地上。

### A Picture is a 1000 Words; A Function is 1000 Lines 《一幅画就是一千字》;一个函数是1000行

: Make the body of every method as long as possible — hopefully you never write any methods or functions with fewer than a thousand lines of code, deeply nested, of course.

：让每个方法的正文尽可能长——希望你永远不会写出少于一千行代码的方法或函数，当然是深度嵌套的。

### One Missing File  一个缺失的文件

: Make sure that one or more critical files is missing. This is best done with includes of includes. For example, in your main module, you have

确保一个或多个关键文件丢失。最好用包含的 includes 来完成。例如，在你的主模块中，你有

```c
#include <stdcode.h>
```

Stdcode.h is available. But in stdcode.h, there’s a reference to

Stdcode.h 是可用的。但在 stdcode.h 中，有一个引用

```c
#include a:\\refcode.h
```

and refcode.h is no where to be found.

而且找不到 refcode.h。

### Write Everywhere, Read Nowhere 到处写，无处读

: At least one variable should be set everywhere and used almost nowhere. Unfortunately, modern compilers usually stop you from doing the reverse, read everywhere, write nowhere, but you can still do it in C or C++.

：至少应设置一个变量 到处都是，几乎没用过。不幸的是，现代编译器通常会阻止你 反过来，到处读书，不写，但你仍然可以做到 C 或 C++。
