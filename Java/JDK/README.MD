# 

## JDK9 反射安全警告

> JDK9之前,Java允许通过反射机制访问所有的成员，这些成员的类型包括私有(private)，公共(public)，包(< package >)和受保护(protected)。

从JDK9开始，对于非公有的成员、成员方法和构造方法，模块不能通过反射直接去访问，但是JDK9提供了一个可选的修饰符open来声明一个开放模块，可以从一个开放模块中导出所有的包，以便在运行时对该模块中的所有包中的所有类型进行深层反射来访问。

```log
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.eclipse.paho.client.mqttv3.internal.FileLock (file:/xxx.jar) to method sun.nio.ch.FileLockImpl.release()
WARNING: Please consider reporting this to the maintainers of org.eclipse.paho.client.mqttv3.internal.FileLock
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```

```java
    public static void main(String[] args) {
        disableWarning();
        SpringApplication.run(Application.class, args);
    }
    public static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
        }
    }
```
