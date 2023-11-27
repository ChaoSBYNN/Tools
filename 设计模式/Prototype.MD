# Prototype 原型模式

## 浅拷贝

```java
public class XxxObject implements Cloneable {

    @Override
    public XxxObject clone() throws CloneNotSupportedException {
        return (XxxObject) super.clone();
    }
}
```

## 深拷贝

```java
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
```
