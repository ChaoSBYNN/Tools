# File


java 文件写入
在Java中，文件的写入可以通过多种方式实现，以下是一些常见的方法：

使用java.io.FileWriter类：

```java
import java.io.FileWriter;
import java.io.IOException;
 
public class Main {
    public static void main(String[] args) {
        try {
            FileWriter writer = new FileWriter("output.txt");
            writer.write("Hello, World!");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

使用java.nio.file.Files类和java.nio.file.Paths类：

```java
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
 
public class Main {
    public static void main(String[] args) {
        try {
            Files.write(Paths.get("output.txt"), "Hello, World!".getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

使用java.io.PrintWriter类：

```java
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
 
public class Main {
    public static void main(String[] args) {
        try {
            PrintWriter writer = new PrintWriter(new File("output.txt"));
            writer.println("Hello, World!");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

以上三种方法都可以实现文件的写入，你可以根据实际需求选择合适的方法。
