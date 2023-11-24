# Singleton 单例模式

> 单例模式用来创建全局唯一的对象。一个类只允许创建一个对象（或者叫实例），那这个类就是一个单例类，这种设计模式就叫作单例模式。单例有几种经典的实现方式，它们分别是：饿汉式、懒汉式、双重检测、静态内部类、枚举。

尽管单例是一个很常用的设计模式，在实际的开发中，我们也确实经常用到它，但是，有些人认为单例是一种反模式（anti-pattern），并不推荐使用，主要的理由有以下几点：

- 单例对OOP特性的支持不友好
- 单例会隐藏类之间的依赖关系
- 单例对代码的扩展性不友好
- 单例对代码的可测试性不友好
- 单例不支持有参数的构造函数

## 1. 私有构造器,防止外部new实例 `非线程安全`

```java
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
```

## 2. 每次获取对象, 都对当前的对象加锁 `线程安全`

> 虽然线程安全,但是每次调用getInstance获得实例的时候,都会对当前的对象进行加锁, 注意,是每一次,这样的话多线程环境下性能会有所下降

```java
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
```

## 3. 只在第一次创建实例对象的时候加锁 `线程安全`

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

## 4. 内部静态工厂类提前创建好实例, JVM保证instance只创建一次, `线程安全`

```java
    private static class SingletonFactory{
        private static Singleton instance = new Singleton();
    }

    public static Singleton getInstance(){
        return SingletonFactory.instance;
    }
```

## 5. DCL(双重检查锁)

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
