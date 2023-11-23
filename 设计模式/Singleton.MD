# Singleton 单例模式

1. 私有构造器,防止外部new实例 `非线程安全`

```java
    public static Singleton1 getInstance() {
        if (instance == null) {
            instance = new Singleton1();
        }
        return instance;
    }
```

2. 每次获取对象, 都对当前的对象加锁 `线程安全`

> 虽然线程安全,但是每次调用getInstance获得实例的时候,都会对当前的对象进行加锁, 注意,是每一次,这样的话多线程环境下性能会有所下降

```java
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
```

3. 只在第一次创建实例对象的时候加锁 `线程安全`

> 单例模式的实例是全局只创建一次的 因此,这个锁不应该在每次获取实例的时候加锁,而应该加在第一次创建对象上

```java
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class){
                if(instance == null){
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
```

4. 内部静态工厂类提前创建好实例, JVM保证instance只创建一次, `内部静态工厂类提前创建好实例，JVM保证instance只创建一次，且线程安全`

```java
    private static class SingletonFactory{
        private static Singleton instance = new Singleton();
    }

    public static Singleton getInstance(){
        return SingletonFactory.instance;
    }
```

5. DCL(双重检查锁)

```java
    private static volatile Singleton instance = null;

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class){
                if(instance == null){
                    instance = new Singleton();
                }
                return instance;
            }
        }
        return instance;
    }
```
