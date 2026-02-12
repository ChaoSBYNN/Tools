## Ambiguity  歧义

> Ambiguity is the mother of confusion.
> 模糊是混乱的源头。
> Roedy 罗迪

The greatest skill comes in writing unmaintainable code that even you can’t figure out a short time after you write it. The key is ambiguity.

最伟大的技能是写出连你都无法维护的代码 写完后不久我搞不清楚。关键是 模糊不清。

Use the fuzziest, vaguest most general terminology you can come up with, especially for variable names. handle is a great example — a handle to what? processData is a great method name. Was there ever a method written that could not be so described? It cleverly hides any clue to what it does behind a cloud of ambiguity.

用你能想到的最模糊、最笼统的术语，尤其是变量名。 手柄就是一个很好的例子——手柄指什么？processData 是个很棒的方法名称。有没有哪种方法写作是无法用这种方式描述的？它巧妙地将任何关于其作用的线索隐藏在一层模糊的云雾后。

So, for example, when you mention the size of a display, make sure you make it abundantly unclear whether you mean to include the margins and/or the menu bar in the size. Mix both definitions in the same program using the same term for both.

比如说，当你提到显示屏的尺寸时，一定要明确说明你是想把边距和/或菜单栏都包含在尺寸里。在同一程序中混合两种定义，使用同一个术语。

If your boss insists you use the unambiguous terms payload size for the display without the margins and Applet size for the total display, subvert him by using just the word size, or use the wrong term occasionally.

如果你的老板坚持让你用明确的术语“payload size”来表示显示器，但不标注边距和小程序大小 为了整体展示，只用字长来颠覆他，或者偶尔用错词。

Make sure, for example, the reader can’t tell whether a variable measures height of a display in lines of text or in pixels or some other unit of measure. Further, you want to seduce the reader into a false sense of security that he does understand. For example, use a variable called lines to measure the height of a text display in pixels.

例如，确保读者无法判断变量是用文字行、像素或其他单位来衡量显示的高度。此外，你还想诱惑读者产生一种他能理解的安全感。例如，使用一个名为线条的变量来测量文本显示的高度（像素单位）。

When dealing with HTML (Hypertext Markup Language) text, make sure the reader can’t tell from the variable name, or the declaration comments if you are dealing with plain ASCII (American Standard Code for Information Interchange) text, high ASCII characters, embedded entities e.g. &amp; or embedded tags e.g. <td>. Juggling all four sorts of beast without any convention for distinguishing them will make any head spin.

处理 HTML（Hypertext Markup Language）文本时，确保读者无法从中区分出来 变量名，或者如果你处理的是普通的声明评论 ASCII（ 梅里坎 Standard C 颂，意为 Information Itterchange）文本，高档 ASCII 字符、嵌入实体（如） 和或嵌入标签（如 <td>）。在没有区分的常规下同时玩四种野兽，会让人头晕目眩。

The Java version 1.4 Collections are a great place for introducing ambiguity. Every Collection is just a bunch of non-descript Objects. Who can tell without a close look what sort of Object is in each Collection? Never use Java version 1.5 generic types because they give away far too many clues. You can make the code even vaguer by using only interface references e.g. Map m and by reusing variables for quite different sorts of Collection.

Java 1.4 版本的集合是引入歧义的好地方。每一个收藏 只是一堆不具特色的对象 s。谁能不仔细观察每个收藏里的物品类型？千万不要用 Java 1.5 版本的通用类型，因为它们会泄露太多线索。你可以通过只使用接口引用（如 Mapm）让代码更加模糊 以及通过重复使用变量来实现截然不同类型的集合 。

The thing a maintenance programmer needs most is a bird’s eye picture of how it all works, a precis of the responsibilities of the various classes. There are so many ways it could work, you must not give away any hints. Make him sweat. Force him to read the details of every last line of code to put together that big picture.

维护程序员最需要的是对整个系统运作的全貌，以及各类职责的概要。有太多可能的可行方式，千万别泄露任何暗示。让他出汗。逼他读完代码的每一行细节，拼凑出那个大局。

The maintenance programmer is always looking for a shortcut — something he can safely ignore. Use ambiguity to give him a false sense of security.

维护程序员总是在寻找捷径——一个他可以安全忽略的。利用模糊性让他产生虚假的安全感。
