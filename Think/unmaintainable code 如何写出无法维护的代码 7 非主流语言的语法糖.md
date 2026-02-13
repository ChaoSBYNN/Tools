## Tricks In Offbeat Languages 非主流语言的语法糖

> Programming in Basic causes brain damage.
> 用 Basic 编程会造成脑损伤。
> Edsger Wybe Dijkstra 埃兹格·怀贝·迪克斯特拉

### SQL (`S`tandard `Q`uery `L`anguage) Aliasing

: Alias table names to one or two letters. Better still alias them to the names of other unrelated existing tables.

：别名表名称可改为一两个字母。更好的是把它们别名到其他无关的现有表上。

### SQL Outer Join SQL 外接

: Mix the various flavours of outer join syntax just to keep everyone on their toes.

混合各种外接语法，让大家保持警觉。

### JavaScript Scope

: Optimise JavaScript code taking advantage of the fact a function can access all local variables in the scope of the caller.

：利用函数可以访问调用者作用域内所有本地变量的特性， 优化 JavaScript 代码。

### Visual Basic Declarations 可视化基本声明

: Instead of:

```shell
dim Count_num as string
dim Color_var as string
dim counter as integer
```

use:

```shell
Dim Count_num$, Color_var$, counter%
```

### Delphi/Pascal Only

: Don’t use functions and procedures. Use the label/goto statements then jump around a lot inside your code using this. It’ll drive ’em mad trying to trace through this. Another idea, is just to use this for the hang of it and scramble your code up jumping to and fro in some haphazard fashion.

：不要使用函数和程序。用 label/goto 语句，然后在代码里多跳转。他们追踪这些会抓狂的。另一个想法是，用它来熟悉，随意地跳来跳去，把代码打乱。

### Perl

: Use trailing if’s and unless’s especially at the end of really long lines.

：在很长的队伍末尾，使用尾随的 if 和 ununless 。

### Lisp

: LISP (`Lis`t `P`rocessing language) is a dream language for the writer of unmaintainable code. Consider these baffling fragments:

： LISP（List Processing language）是作家梦寐以求的语言 无法维护的代码。请看这些令人困惑的片段：

```shell
(lambda (*<8-]= *<8-[= ) (or *<8-]= *<8-[= ))
(defun :-] (<) (= < 2))

(defun !(!)(if(and(funcall(lambda(!)(if(and '(< 0)(< ! 2))1 nil))(1+ !))
(not(null '(lambda(!)(if(< 1 !)t nil)))))1(* !(!(1- !)))))
```

### Visual Foxpro

: This one is specific to Visual Foxpro. A variable is undefined and can’t be used unless you assign a value to it. This is what happens when you check a variable’s type:

：这个专属 Visual Foxpro。变量是未定义的，除非你赋予它一个值，否则无法使用。检查变量类型时会发生这样的情况：

```c
lcx = TYPE('somevariable')
```

The value of lcx will be 'U' or undefined. BUT if you assign scope to the variable it sort of defines it and makes it a logical FALSE. Neat, huh!?

lcx 的价值为“U”或未定义。但是如果你给变量赋值范围，它就在某种程度上定义了它，并使其成为逻辑上的 FALSE。很酷吧！？

```shell
LOCAL lcx
lcx = TYPE('somevariable')
```

The value of lcx is now 'L' or logical. It is further defined the value of FALSE. Just imagine the power of this in writing unmaintainable code.

lcx 的值现在是“L”或逻辑。它进一步定义了 FALSE 的值。想象一下它写出无法维护的代码有多强大。

```shell
LOCAL lc_one, lc_two, lc_three…, lc_n
IF lc_one
DO some_incredibly_complex_operation_that_will_neverbe_executed WITH
make_sure_to_pass_parameters
ENDIF

IF lc_two
DO some_incredibly_complex_operation_that_will_neverbe_executed WITH
make_sure_to_pass_parameters
ENDIF

PROCEDURE some_incredibly_complex_oper…
* put tons of code here that will never be executed
* why not cut and paste your main procedure!
ENDIF
```
