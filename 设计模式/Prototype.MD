# Prototype 原型模式

## 引用拷贝

> 引用拷贝不会在堆上创建一个新的对象，只 会在栈上生成一个新的引用地址，最终指向依然是堆上的`同一个对象`。

```java
XxxObject obj1 = new XxxObject();
XxxObject obj2 = obj1;

```

## 浅拷贝

浅拷贝会在堆上创建一个新的对象，`新对象和原对象不等，但是新对象的属性和老对象相同`。

- 属性是基本类型(int,double,long,boolean等) 拷贝的就是基本类型的值
- 属性是引用类型(除了基本类型都是引用类型) 拷贝的就是引⽤数据类型变量的地址值，⽽对于引⽤类型变量指向的堆中的对象不会拷贝

实现浅拷贝呢？也很简单，就是在需要拷贝的类上实现Cloneable接口并重写其clone()方法

```java
public class XxxObject implements Cloneable {

    @Override
    public XxxObject clone() throws CloneNotSupportedException {
        return (XxxObject) super.clone();
    }
}
```

## 深拷贝

> 完全拷贝⼀个对象，在堆上创建一个新的对象，拷贝被拷贝对象的成员变量的值，同时堆中的对象也会拷贝。 需要重写clone方法

```java
public class XxxObject implements Serializable {
    public XxxObject deepClone() {

        // Anything 都是可以用字节流进行表示，记住是任何！
        XxxObject xxxObject = null;
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将当前的对象写入baos【输出流 -- 字节数组】里
            oos.writeObject(this);

            // 从输出字节数组缓存区中拿到字节流
            byte[] bytes = baos.toByteArray();

            // 创建一个输入字节数组缓冲区
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            // 创建一个对象输入流
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 下面将反序列化字节流 == 重新开辟一块空间存放反序列化后的对象
            xxxObject = (XxxObject) ois.readObject();

        } catch (Exception e) {
            System.out.println(e.getClass() + ":" + e.getMessage());
        }
        return xxxObject;
    }
}
```
