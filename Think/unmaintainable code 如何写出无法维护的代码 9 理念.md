## Philosophy  理念

> Common Sense Is not Obvious 常识并不显而易见
> Common sense is not a simple thing. Instead, it is an immense society of hard-earned practical ideas — of multitudes of life-learned rules and exceptions, dispositions and tendencies, balances and checks.
> 常识不是件简单的事。相反，它是一个庞大的社会，充满了来之不易的实用理念——无数人生学到的规则与例外、性情与倾向、平衡与制衡。
> Marvin Minsky 马文·明斯基

The people who design languages are the people who write the compilers and system classes. Quite naturally they design to make their work easy and mathematically elegant. However, there are 10,000 maintenance programmers to every compiler writer. The grunt maintenance programmers have absolutely no say in the design of languages. Yet the total amount of code they write dwarfs the code in the compilers.

设计语言的人是编写编译器和系统类的人。他们自然而然地设计得轻松且数学上优雅。然而，每个编译器编写者前就有1万名维护程序员。基层维护程序员对语言设计完全没有发言权。然而，他们编写的代码总量远远超过编译器中的代码。

An example of the result of this sort of elitist thinking is the JDBC (Java Data Base Connectivity) interface. It makes life easy for the JDBC implementor, but a nightmare for the maintenance programmer. It is far clumsier than the FØRTRAN interface that came out with SQL (Standard Query Language) three decades ago.

这种精英主义思维的一个例子是 JDBC（Java Data Base Connectivity）接口。这让 JDBC 的工作变得轻松 实现者，但对维护程序员来说简直是噩梦。很远 比推出的 FØRTRAN 接口还要笨拙 三十年前的 SQL（Standard Query Language）。

Maintenance programmers, if somebody ever consulted them, would demand ways to hide the housekeeping details so they could see the forest for the trees. They would demand all sorts of shortcuts so they would not have to type so much and so they could see more of the program at once on the screen. They would complain loudly about the myriad petty time-wasting tasks the compilers demand of them.

如果有人向维护程序员请教，他们会要求隐藏家务细节，以便能看到全貌。他们会要求各种快捷方式，这样就不用打那么多字，也能同时在屏幕上看到更多程序内容。他们会大声抱怨编译器要求的无数琐碎浪费时间的任务。

There are some efforts in this direction: NetRexx, Bali and visual editors (e.g. IBM (International Business Machines) ’s Visual Age is a start) that can collapse detail irrelevant to the current purpose.

在这方面有一些努力：NetRexx、Bali 和视觉编辑器（例如 IBM（I nternational Business Machines）的 Visual Age 就是一个起点），可以压缩与当前目的无关的细节。
