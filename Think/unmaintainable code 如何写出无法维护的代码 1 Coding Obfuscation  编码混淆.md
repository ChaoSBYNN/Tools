## Coding Obfuscation  编码混淆

> Sedulously eschew obfuscatory hyperverbosity and prolixity.
> 勤勉地避免晦涩的冗长和冗长。
> Anonymous

### XML (e`x`tensible `M`arkup `L`anguage)

The XML fad has created a bonanza of opportunities for obfuscation. The basic technique is to pick a random hunk of code, then invent an obscure way of representing its logic in XML. Then replace the piece of code with an XML properties file and an XML parser. Make sure the XML representation you choose is so limited that almost anything other than the original logic cannot be expressed in it. Of course, you never document the XML language extension or the parser. Nobody questions the simplicity of XML. Using this technique, you should easily be able to balloon 10 lines of simple Java code up to 100 lines of perfectly opaque XML.

该 XML 潮流 创造了大量混淆视听的机会。基本技巧是选择一个 随机代码块，然后发明一种晦涩的方式来表示其逻辑 然后用 XML 属性文件和 XML 解析器替换这段代码。确保 XML 是 你选择的代表性极其有限，几乎除了原作之外的表现 逻辑无法用它表达。当然，你永远不会记录 XML 语言 扩展或解析器。没人质疑它的简单性 用这种方法，你应该能轻松把 10 行简单的 Java 代码膨胀到 100 行完全不透明的 XML 上。

### Obfuscated C  混淆 C

Follow the obfuscated C contests on the Internet and sit at the lotus feet of the masters.

关注网络上那些晦涩难懂的 C 级比赛，坐在大师们的莲花脚下。

### Find a Forth or APL (A Programming Language) Guru

In those worlds, the terser your code and the more bizarre the way it works, the more you are revered.

在那些世界里，代码越简洁、越离奇，你就越受尊敬。

### I’ll Take a Dozen 我要一打

Never use one housekeeping variable when you could just as easily use two or three.

千万不要只用一个常用变量，你完全可以用两三个变量。

### Jude the Obscure

Always look for the most obscure way to do common tasks. For example, instead of using arrays to convert an integer to the corresponding string, use code like this:

总是寻找最冷门的常见任务方式。例如，与其用数组将整数转换为对应字符串，不如使用如下代码：

```c++
char *p;
switch (n)
{
case 1:
    p = one;
    if (0)
case 2:
    p = two;
    if (0)
case 3:
    p = three;
    printf(%s, p);
    break;
}
```

### Foolish Consistency Is the Hobgoblin of Little Minds 愚蠢的一致性约定

When you need a character constant, use many different formats: ' ', 32, 0x20, 040. Make liberal use of the fact that 10 and 010 are not the same number in C or Java.

当你需要一个字符常数时，可以使用多种格式：' '、32、0x20、040。充分利用 10 和 010 在 C 语言或 Java 语言中不同数字的事实。

### Casting

Pass all data as a void * and then typecast to the appropriate structure. Using byte offsets into the data instead of structure casting is fun too.

将所有数据都作为空*传递，然后类型化为相应的结构。用字节偏移量进入数据，代替结构投射也很有趣。

### The Nested Switch  嵌套Switch

(a switch within a switch) is the most difficult type of nesting for the human mind to unravel.

（switch中的switch关）是人类大脑最难解开的嵌套类型。

### Exploit Implicit Conversion 利用隐式转换

Memorize all of the subtle implicit conversion rules in the programming language. Take full advantage of them. Never use a picture variable (in COBOL or PL/I) or a general conversion routine (such as sprintf in C). Be sure to use floating-point variables as indexes into arrays, characters as loop counters and perform string functions on numbers. After all, all of these operations are well-defined and will only add to the terseness of your source code. Any maintainer who tries to understand them will be very grateful to you because they will have to read and learn the entire chapter on implicit data type conversion; a chapter that they probably had completely overlooked before working on your programs.

记住编程语言中所有隐含的转换规则。充分利用它们。切勿使用图像变量（COBOL 或 PL/I）或通用转换例程（如 C 语言中的 sprintf）。一定要用浮点变量作为数组索引，字符作为循环计数器，并对数字执行字符串函数。毕竟，所有这些作都是明确定义的，只会让你的源代码更简洁。任何试图理解这些内容的维护者都会非常感激你，因为他们必须阅读并学习关于隐式数据类型转换的整章;他们在参与你的项目之前，可能完全忽略了这一章节。

### int literals  隐藏

When using ComboBoxes, use a switch statement with integer cases rather than named constants for the possible values.

使用 ComboBox 时，使用整数大小写的 switch 语句，而不是命名常量来表示可能的值。

If you have an array with 100 elements in it, hard code the literal 100 in as many places in the program as possible. Never use a static final named constant for the 100, or refer to it as myArray.length. To make changing this constant even more difficult, use the literal 50 instead of 100/2, or 99 instead of 100-1. You can further disguise the 100 by checking for a == 101 instead of a > 100 or a > 99 instead of a >= 100.

如果你有一个包含 100 个元素的数组，尽可能在程序中把字面上的 100 个元素硬编码。切勿为 100 使用静态的最终命名常量，也不要称之为 myArray.length。为了让改变这个常数更难，可以用字面上的 50 代替 100/2，或者 99 代替 100-1。你还可以进一步伪装 100，比如检查==101 而不是%3E 100，或者用> 99 代替 a >= 100。

Consider things like page sizes, where the lines consisting of x header, y body, and z footer lines, you can apply the obfuscations independently to each of these and to their partial or total sums.

考虑页面大小，其中由 x 个头、y 个正体组成的行， Z 页脚行，你可以分别对这些路径应用混淆 以及部分或总和。

These time-honoured techniques are especially effective in a program with two unrelated arrays that just accidentally happen to both have 100 elements. If the maintenance programmer has to change the length of one of them, he will have to decipher every use of the literal 100 in the program to determine which array it applies to. He is almost sure to make at least one error, hopefully one that won’t show up for years later.

这些历久弥新的技巧在两个无关的阵列中尤其有效，这两个阵列恰好都拥有100个元素。如果维护程序员需要更改其中一个数组的长度，他必须破译程序中每一次字面意义上的100，以确定它适用于哪个阵列。他几乎肯定会犯至少一次错误，希望多年后才会出现。

There are even more fiendish variants. To lull the maintenance programmer into a false sense of security, dutifully create the named constant, but very occasionally accidentally use the literal 100 value instead of the named constant. Most fiendish of all, in place of the literal 100 or the correct named constant, sporadically use some other unrelated named constant that just accidentally happens to have the value 100, for now. It almost goes without saying that you should avoid any consistent naming scheme that would associate an array name with its size constant.

还有更邪恶的变体。用来让维护程序员陷入 虚假的安全感，尽职尽责地创造了命名的常数，但极少数 不小心用了字面上的 100 值代替命名的常数。最恶毒的是，暂时用一个无关的无关常数代替字面上的 100 或正确的命名常数，而恰好值为 100。几乎不用说，你应该避免任何一致的命名方案，避免将数组名称与其大小常数关联起来。

### Semicolons!  分号！

Always use semicolons whenever they are syntactically allowed. For example:

只要语法允许，务必使用分号。例如：

```java
if ( a );
else;
{
   int d;
   d = c;
}
```

### Use Octal For Obscurity 使用八进制来保持模糊

Smuggle octal literals into a list of decimal numbers like this:

把八进制字面量偷偷放进一个十进制数字列表，比如这样：

```java
array = new int[]
{
   111,
   120,
   013,
   121,
};
```

### Convert Indirectly  间接转换

Java offers great opportunity for obfuscation whenever you have to convert. As a simple example, if you have to convert a double to a String, go circuitously, via Double with new Double(d).toString() rather than the more direct Double.toString(d). You can, of course, be far more circuitous than that! Avoid any conversion techniques recommended by the Conversion Amanuensis. You get bonus points for every extra temporary object you leave littering the heap after your conversion.

Java 提供了很棒的选择 每当你需要转换时，都有混淆的机会。举个简单的例子，如果你 必须将双重转换为字符串 ，绕行，通过双重 ，使用 新的 Double（d）.toString（） 而非更直接的 Double.toString（d）。当然，你也可以绕得更远！避免使用转换专员推荐的任何转换技巧。转换后你在堆积堆里多留一个临时物品，都会获得额外加分。

### Nesting  嵌套

Nest as deeply as you can. Good coders can get up to 10 levels of ( ) on a single line and 20 { } in a single method. C++ coders have the additional powerful option of preprocessor nesting totally independent of the nest structure of the underlying code. You earn extra Brownie points whenever the beginning and end of a block appear on separate pages in a printed listing. Wherever possible, convert nested ifs into nested [? : ] ternaries. If they span several lines, so much the better.

尽可能深地嵌套。优秀的程序员能起身 在单行中可达 10 级（ ）和单一方法中 20 级 { }。C++ 程序员还有一个更强大的选项，可以完全独立于底层代码的嵌套结构进行预处理器嵌套。每当一个块的开头和结尾出现在印刷列表中的不同页面时，你就能获得额外的好评。尽可能将嵌套的 ifs 转换为嵌套的 [？： ] 三条。如果它们跨越多行，那就更好了。

### C’s Eccentric View of Arrays C 的偏心数组视图

C compilers transform `myArray[i]` into `*(myArray + i)`, which is equivalent to `*(i + myArray)` which is equivalent to `i[myArray]`. Experts know to put this to good use. To really disguise things, generate the index with a function:

C 编译器进行变换 `myArray[i]` 变为 `*（myArray + i）`， 等价于 `*（i + myArray）`， 后者等价于 `i[myArray]`。 专家们知道如何好好利用这一点。To 真正伪装一下，用函数生成索引：

```c++
int myfunc(int q, int p) { return p%q; }
int myfunc（int q， int p） { return p%q; }
…
myfunc(6291, 8)[Array];  myfunc（6291， 8）[Array];
```

Unfortunately, these techniques can only be used in native C classes, not Java.

遗憾的是，这些技术只能在原生 C 类中使用，Java 无法使用。

### L o n g L i n e s 超长

Try to pack as much as possible into a single line. This saves the overhead of temporary variables and makes source files shorter by eliminating new line characters and white space. Tip: remove all white space around operators. Good programmers can often hit the 255 character line length limit imposed by some editors. The bonus of long lines is that programmers who cannot read 6 point type must scroll to view them.

尽量把尽可能多的内容塞进一条线里。这节省了临时变量的开销，并通过消除新行字符和留白使源文件更短。提示：去除周围的所有空白。优秀的程序员通常能达到部分编辑器设定的255字符行长限制。长行的好处是，程序员看不懂六点字的程序员必须滚动才能查看。

### Exceptions  例外情况

I am going to let you in on a little-known coding secret. Exceptions are a pain in the behind. Properly-written code never fails, so exceptions are actually unnecessary. Don’t waste time on them. Subclassing exceptions is for incompetents who know their code will fail. You can greatly simplify your program by having only a single try/catch in the entire application (in main) that calls System.exit(). Just stick a perfectly standard set of throws on every method header whether they could actually throw any exceptions or not.

我要告诉你一个鲜为人知的编程秘密。例外真是让人头疼。正确编写的代码从不失败，因此例外实际上是不必要的。别浪费时间在他们身上。子类例外是给那些知道代码会失败的无能者。你可以通过在整个应用程序（主程序）中只有一个调用 System.exit（） 的尝试/捕获，大大简化程序。只要在每个方法头上都装一套完全标准的抛出，不管它们是否能抛出异常。

### When To Use Exceptions 何时使用例外

Use exceptions for non-exceptional conditions. Routinely terminate loops with an ArrayIndexOutOfBoundsException. Pass return standard results from a method in an exception.

使用例外 非特殊情况。例行地用 ArrayIndexOutOfBoundsException 终止循环。Pass return 标准是异常中的某个方法的结果。

### `Efficient` Exceptions 高效 例外情况

Throwing an Exception has quite a high overhead. The `JVM` (`J`ava `V`irtual `M`achine) has to scan the stack looking for a ton of information to potentially use in a stack trace. You can avoid this overhead by constructing a Exception object once and throwing it many times. The stack trace will be for the spot in the code where the Exception was constructed, not where it was thrown. This will really keep them guessing where the bugs are.

抛出异常的开销相当高。`JVM` (`J`ava `V`irtual `M`achine) 必须扫描堆栈，寻找大量信息 可能用于栈跟踪。你可以通过构建一个 异常对象一次，然后多次抛出。栈跟踪会针对异常构建的代码位置，而不是抛出异常的位置。 这真的会让他们一直猜测虫子藏在哪里。

### Use threads With Abandon 随意使用线程

title says it all.

标题已经说明了一切。

### Lawyer Code  律师规范

Follow the language lawyer discussions in the newsgroups about what various bits of tricky code should do e.g. a=a++; or f(a++,a++); then sprinkle your code liberally with the examples. In C, the effects of pre/post decrement code such as

请关注律师讨论中的语言 新闻组讨论各种棘手代码应该做什么，比如 A=A++; 或者 F（A++，A++）; 然后撒上你的代码 举例非常丰富。在 C 语言中，衰减前后代码的影响，例如：

```c
*++b ? (*++b + *(b-1)) : 0
*++b ？（*++b + *（b-1）） ： 0
```

are not defined by the language spec. Every compiler is free to evaluate in a different order. This makes them doubly deadly. Similarly, take advantage of the complex tokenizing rules of C and Java by removing all spaces.

这些都不由语言规范定义。每个编译器都可以自由以不同顺序进行评估。这使得它们的致命性加倍。同样，利用 C 和 Java 复杂的分词规则，去除所有空间。

### Early Returns  早期回归

Rigidly follow the guidelines about no goto, no early returns and no labeled breaks especially when you can increase the if/else nesting depth by at least 5 levels.

严格遵守无去、不提前退货和不标记断层的指导原则，尤其是当你可以将 if/else 巢穴深度至少增加 5 级时。

### Avoid {}  避免 {}

Never put in any { } surrounding your if/else blocks unless they are syntactically obligatory. If you have a deeply nested mixture of if/else statements and blocks, especially with misleading indentation, you can trip up even an expert maintenance programmer. For best results with this technique, use Perl. You can pepper the code with additional ifs after the statements, to amazing effect.

千万不要在你的 if/else 周围加上任何{ } 除非是语法上的强制性，否则都用来设置块。如果你的混合物是深度嵌套的 关于 if/else 语句和块，尤其是带有误导性缩进的，你可以 即使是专业的维护程序员也会绊倒。为了获得最佳效果， 用 Perl 吧。你可以在代码中添加额外的“如果”，效果非常棒。

### Tabs From Hell  地狱来的Tabs缩进

Never underestimate how much havoc you can create by indenting with tabs instead of spaces, especially when there is no corporate standard on how much indenting a tab represents. Embed tabs inside string literals, or use a tool to convert spaces to tabs that will do that for you.

永远不要低估用制表符缩进而非空格会带来多大的混乱，尤其是在公司没有统一标准规定制表表的缩进程度时。把制表符嵌入字符串字面量里，或者用工具把空格转换成制表表，这样就能帮你实现这个效果。

### Magic Matrix Locations  魔法矩阵

Use special values in certain matrix locations as flags. A good choice is the [3][0] element in a transformation matrix used with a homogeneous coordinate system.

在某些矩阵位置使用特殊值作为标志。一个不错的选择是变换矩阵中的[3][0]元素，该矩阵与齐次坐标系一起使用。

### Magic Array Slots revisited 魔法矩阵再现

If you need several variables of a given type, just define an array of them, then access them by number. Pick a numbering convention that only you know and don’t document it. And don’t bother to define #define constants for the indexes. Everybody should just know that the global variable widget[15] is the cancel button. This is just an up-to-date variant on using absolute numerical addresses in assembler code.

如果你需要某个类型中的多个变量，只需定义一个数组，然后按数字访问它们。选择只有你知道且不记录的编号规范。而且不用费心去定义索引的 #define 常数。大家应该知道全局变量控件[15]就是取消按钮。这只是汇编代码中使用绝对数值地址的一种最新变体。

### Never Beautify  永远不要美化

Never use an automated source code tidier (beautifier) to keep your code aligned. Lobby to have them banned them from your company on the grounds they create false deltas in PVCS/CVS (version control tracking) or that every programmer should have his own indenting style held forever sacrosanct for any module he wrote. Insist that other programmers observe those idiosyncratic conventions in his modules. Banning beautifiers is quite easy, even though they save the millions of keystrokes doing manual alignment and days wasted misinterpreting poorly aligned code. Just insist that everyone use the same tidied format, not just for storing in the common repository, but also while they are editing. This starts an RWAR (Religious War) and the boss, to keep the peace, will ban automated tidying. Without automated tidying, you are now free to accidentally misalign the code to give the optical illusion that bodies of loops and ifs are longer or shorter than they really are, or that else clauses match a different if than they really do, e. g.

永远不要使用自动源代码整理器 （美化师）为了让你的代码保持一致。游说让他们在你的账户中被封禁 公司认为他们在 PVCS/CVS 中制造了虚假的差值（版本控制） 追踪）或者每个程序员都应该拥有自己独特的缩进风格，永远保持 对他写的任何模块来说都是神圣不可侵犯的。坚持让其他程序员遵守这些 他的模块中采用了独特的惯例。禁用美化器其实很容易，尽管它们节省了数百万次手动对齐的按键和误解对齐代码的数天。只要坚持大家用同一个整齐的格式，而不仅仅是存放 公共仓库，也包括编辑时。这开始了 RWAR（ 宗教战争 ）和 Boss 为了维持和平，会禁止自动清理。没有自动整理，你现在可以自由地把代码错位，从而产生视觉错觉 循环和 IF 的体比实际长度或短，或者其他原因 条款匹配的“如果”与实际匹配不同，例如：

```c
// note misleading alignment
if ( a )
   if ( b ) x=y;
else x=z;

// the proper alignment is
if ( a )
   if ( b ) x=y;
   else x=z;

// if the two examples look the same, please notify me.
// Sometimes I inadvertently fix the misaligned example
// with a bulk tidy.
// The top one should look like this:
// if ( a )
//   if ( b ) x=y;
// else x=z;
```

### The Macro Preprocessor 宏预处理器

It offers great opportunities for obfuscation. The key technique is to nest macro expansions several layers deep so that you have to discover all the various parts in many different *.hpp files. Placing executable code into macros then including those macros in every *.cpp file (even those that never use those macros) will maximize the amount of recompilation necessary if ever that code changes.

它提供了极好的混淆视听机会。关键技巧是将宏扩展嵌套到几层深层，这样你就必须在多个不同的 *.hpp 文件中发现所有不同的部分。把可执行代码放进宏里，然后把这些宏放进每个*.cpp 文件（即使是那些从未用过这些宏的）里，这样如果代码有变动，所需的重新编译次数会最大化。

### Exploit Schizophrenia

Java is schizophrenic about array declarations. You can do them the old C, way String x[], (which uses mixed pre-postfix notation) or the new way String[] x, which uses pure prefix notation. If you want to really confuse people, mix 

Java 对数组声明非常矛盾。你可以用旧的 C 语言，用词缀 x[]（用混合前缀符号）或者新法的词缀 x，用纯前缀符号。如果你想真正让人困惑，就混在一起

```java
byte[] rowvector, colvector, matrix[];
```

which

```java
byte[] rowvector;
byte[] colvector;
byte[][] matrix;
```

### Hide Error Recovery Code  隐藏错误恢复代码

Use nesting to put the error recovery for a function call as far as possible away from the call. This simple example can be elaborated to 10 or 12 levels of nest:  

使用嵌套将函数调用的错误恢复尽可能远离调用。这个简单的示例可以扩展到 10 或 12 层嵌套：

```java
if ( function_A() == OK )
   {
   if ( function_B() == OK )
      {
      /* Normal completion stuff */
      }
   else
      {
      /* some error recovery for Function_B */
      }
   }
else
   {
   /* some error recovery for Function_A */
   }
```

### Pseudo C  伪 C

The real reason for #define was to help programmers who are familiar with another programming language to switch to C. Maybe you will find declarations like #define begin { " or " #define end } useful to write more interesting code.  

#define 的真实原因是为了帮助熟悉其他编程语言的程序员转而使用 C 语言。也许你会发现像 #define begin { " 或 " #define end } 这样的声明对编写更有趣的代码很有用。

### Confounding Imports  混淆导入

Keep the maintenance programmer guessing about what packages the methods you are using are in. Instead of:  

让维护程序员猜测你使用的那些方法在哪个包中。而不是：

```java
import com.mindprod.mypackage.Read;
import com.mindprod.mypackage.Write;
```

use

```java
import com.mindprod.mypackage.*;
```

Never fully qualify any method or class no matter how obscure. Let the maintenance programmer guess which of the packages/classes it belongs to. Of course, inconsistency in when you fully qualify and how you do your imports helps most.  

永远不要完全限定任何方法或类，无论多么晦涩。让维护程序员去猜测它属于哪个包/类。当然，在何时完全限定以及如何进行导入方面的一致性最有帮助。

### Toilet Tubing  马桶管道

Never under any circumstances allow the code from more than one function or procedure to appear on the screen at once. To achieve this with short routines, use the following handy tricks:   

在任何情况下都绝对不允许一个函数或过程的代码同时出现在屏幕上。对于简短的程序，可以使用以下便捷技巧来实现这一点：

* Blank lines are generally used to separate logical blocks of code. Each line is a logical block in and of itself. Put blank lines between each line.  
* 通常使用空行来分隔代码的逻辑块。每一行本身就是逻辑块。在每一行之间添加空行。
* Never comment your code at the end of a line. Put it on the line above. If you’re forced to comment at the end of the line, pick the longest line of code in the entire file, add 10 spaces and left-align all end-of-line comments to that column.  
* 绝不要在行的末尾添加注释。将其放在上一行。如果你被迫在行末添加注释，选择整个文件中最长的代码行，添加 10 个空格，并将所有行尾注释左对齐到该列。
* Comments at the top of procedures should use templates that are at least 15 lines long and make liberal use of blank lines. Here’s a handy template:  
* 在过程顶部应使用至少 15 行的模板，并大量使用空行。这里有一个方便的模板：

```java
/*
/* Procedure Name:  
/*
/* Original procedure name:  
/*  
/* Author:  
/*
/* Date of creation:  
/*
/* Dates of modification:  
/*  
/* Modification authors:  
/*
/* Original file name:  
/*
/* Purpose:  
/*  
/* Intent:  
/*
/* Designation:  
/*
/* Classes used:  
/*  
/* Constants:  
/*
/* Local variables:  
/*
/* Parameters:  
/*  
/* Date of creation:  
/*
/* Purpose:  
*/
```

The technique of putting so much redundant information in documentation almost guarantees it will soon go out of date and will help befuddle maintenance programmers foolish enough to trust it. 

在文档中放入大量冗余信息的技术几乎可以保证它很快就会过时，并且会帮助那些愚蠢到相信它的维护程序员感到困惑。

### Encapsulate The Trivial  封装琐碎

Create entire classes or methods to encapsulate trivialities that could never possibly change, but which then require complex invocation and careful unravelling to discover that the code does almost nothing. Here is a classic

为那些永远不会改变但需要复杂的调用和仔细的解构才能发现代码几乎什么都没做的琐碎事物创建整个类或方法。这里有一个经典案例

```java
class Truth
   {
   boolean isTrue ( boolean assertion )
      {
      if ( assertion != false )
         {
         return assertion;
         }
      else
         {
         return assertion;
         }
      }
   }
...

boolean doIt;

Truth trutherizer = new Truth();
if ( trutherizer.isTrue( s.equals( t ) ) )
   {
   doIt = true;
   }
else
   {
   doIt = false;
   }

// hint: all the above accomplishes is:
doIt = s.equals( t );
```

### Loops 循环

The humble canonical for loop: for (int i=0; i<n; i++ ) should never be used. Always randomly disguise it, for example by:

谦逊的规范 for 循环：for （int i=0; i<n; i++ ） 绝不应使用。总是这样 可以随机伪装，例如：

* Redoing it as a while or do while loop.
* 有时重做 ，或者做 while 循环。
* Reversing the names of the i and n variables, or making up fanciful names for either that have nothing to do with their purpose as index and count.
* 比如把 i 和 n 变量的名字反过来，或者给它们编造与它们作为索引和计数无关的奇想名字。
* Changing the < to <=.
* 将<改为<=。
* Use i-- just for a change of pace.
* 用 i—— 换换口味。

It goes without saying you should never use the compact for:each loop. There many ways to rearrange the parts of an Iterator loop over a Collection so every time the maintenance programmer looks at on a simple Iterator, it appears to be something novel.

不用说，你绝不应该在每个循环中使用紧凑型。有许多方法可以重新排列 迭代器循环于一个集合 ，所以每次维护程序员在简单的迭代器上看到时，它看起来都是新颖的东西。
