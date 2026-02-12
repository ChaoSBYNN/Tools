## Testing  测试

> I don’t need to test my programs. I have an error-correcting modem.
> 我不需要测试我的程序。我有一台纠错调制解调器。
> Om I. Baud  欧姆·I·鲍德

Leaving bugs in your programs gives the maintenance programmer who comes along later something interesting to do. A well done bug should leave absolutely no clue as to when it was introduced or where. The laziest way to accomplish this is simply never to test your code.

在程序中留下漏洞，给后来的维护程序员提供了有趣的事情可做。一个做得好的漏洞应该完全不留任何线索，说明它是什么时候或在哪里出现的。最懒惰的做法就是永远不测试你的代码。

### Never Test  永不测试

: Never test any code that handles the error cases, machine crashes, or OS (Operating System) glitches. Never check return codes from the OS. That code never gets executed anyway and slows down your test times. Besides, how can you possibly test your code to handle disk errors, file read errors, OS crashes and all those sorts of events? Why, you would have to either an incredibly unreliable computer or a test scaffold that mimicked such a thing. Modern hardware never fails and who wants to write code just for testing purposes? It isn’t any fun. If users complain, just blame the OS or hardware. They’ll never know.

： 机器，永远不要测试处理错误情况的代码 崩溃 ，或者 OS（Operating System）故障。永远不要查看作系统的退货码。这些代码根本不会被执行，反而会拖慢你的测试时间。而且，你怎么可能测试代码来处理磁盘错误、文件读取错误、 作系统崩溃等情况呢？你会的 要么是极其不可靠的电脑，要么是模仿的测试支架 真是这样。现代硬件从不出错，谁愿意专门写代码 测试目的？一点也不好玩。如果用户抱怨，就怪罪他们 作系统还是硬件。他们永远不会知道。

### Never, Ever Do Any Performance Testing 绝对不要做任何性能测试

: Hey, if it isn’t fast enough, just tell the customer to buy a faster machine. If you did do performance testing, you might find a bottleneck, which might lead to algorithm changes, which might lead to a complete redesign of your product. Who wants that? Besides, performance problems that crop up at the customer site mean a free trip for you to some exotic location. Just keep your shots up-to-date and your passport handy.

： 嘿，如果不够快，就告诉顾客买更快的机器。如果你做了性能测试，可能会遇到瓶颈，进而导致算法改变，进而彻底重新设计你的产品。谁想要那种？此外，客户现场出现的性能问题意味着你免费去某个异国风情的地方。只要保持疫苗更新，护照随身携带就行。

### Never Write Any Test Cases 永远不要写任何测试用例

: Never perform code coverage or path coverage testing. Automated testing is for wimps. Figure out which features account for 90% of the uses of your routines and allocate 90% of the tests to those paths. After all, this technique probably tests only about 60% of your source code and you have just saved yourself 40% of the test effort. This can help you make up the schedule on the back-end of the project. You’ll be long gone by the time anyone notices that all those nice marketing features don’t work. The big, famous software companies test code this way; so should you. And if for some reason, you are still around, see the next item.

：绝不执行代码覆盖或路径覆盖 测试。自动化测试是给软柿子用的。找出哪些特征是 你例行程序的 90% 使用率，并且将 90% 的测试分配给这些路径。毕竟，这种方法可能只测试大约 60% 的源代码，而你却节省了 40%的测试精力。这有助于你在项目后期安排进度。等到有人发现那些漂亮的营销功能都没用时，你早就离开了。大型知名软件公司就是用这种方式测试代码;你也应该。如果你还在，请看下一个项目

### Testing is for cowards 测试是懦夫的事

: A brave coder will bypass that step. Too many programmers are afraid of their boss, afraid of losing their job, afraid of customer hate mail and afraid of being sued. This fear paralyzes action and reduces productivity. Studies have shown that eliminating the test phase means that managers can set ship dates well in advance, an obvious aid in the planning process. With fear gone, innovation and experimentation can blossom. The rôle of the programmer is to produce code and debugging can be done by a cooperative effort on the part of the help desk and the legacy maintenance group.

：勇敢的程序员会跳过这一步。太多程序员害怕老板，害怕失业，害怕客户的仇恨邮件，害怕被起诉。这种恐惧使行动瘫痪，降低生产力。研究表明，取消测试阶段意味着管理者可以提前很长时间确定发货日期，这对规划过程有明显帮助。恐惧消失后，创新和实验才能绽放。程序员的角色是产出代码，调试可以通过帮助台和遗留维护团队的合作完成。

If we have full confidence in our coding ability, then testing will be unnecessary. If we look at this logically, then any fool can recognise that testing does not even attempt to solve a technical problem, rather, this is a problem of emotional confidence. A more efficient solution to this lack of confidence issue is to eliminate testing completely and send our programmers to self-esteem courses. After all, if we choose to do testing, then we have to test every program change, but we only need to send the programmers to one course on building self-esteem. The cost benefit is as amazing as it is obvious.

如果我们对自己的编码能力充满信心，那么测试就不再必要。如果我们用逻辑来看，任何傻瓜都能看出测试根本不是在解决技术问题，而是情感自信的问题。解决这种缺乏自信问题的更有效方案是完全取消测试，让程序员去参加自尊课程。毕竟，如果我们选择做测试，就必须测试每一个程序变更，但我们只需要让程序员参加一门关于建立自尊的课程。成本效益和显著的收益一样惊人。

### Ensuring It Only Works In Debug Mode 确保它只在调试模式下工作

: If you’ve defined TESTING as 1

：如果你把测试定义为1

```c
#define TESTING 1
```

this gives you the wonderful opportunity to have separate code sections, such as

这为你提供了极好的机会，可以设置独立的代码部分，比如

```c
#if TESTING==1
#endif
```

which can contain such indispensable tidbits as

其中可能包含以下不可或缺的细节

```shell
x = rt_val;
```
so that if anyone resets TESTING to 0, the program won’t work. And with the tiniest bit of imaginative work, it will not only befuddle the logic, but confound the compiler as well.

这样如果有人把 TESTING 重置为 0，程序就无法工作。只要稍有创意，不仅会让逻辑变得困惑，也会让编译器困惑。
