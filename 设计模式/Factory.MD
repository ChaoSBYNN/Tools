# Factory 工厂模式

## 1. 使用单工厂模式 `IF-KEY`

```java
public class BizFactory {
    public IBiz createBiz(String key) {
        if ("BizA".equals(key)) {
            return new BizA();
        } else if ("BizB".equals(key)) {
            return new BizC();
        }else{
            return null;
        }
    }
}
```
## 2. 多方法工厂模式

```java
public class BizFactory {
    public IBiz createBizA() {
        return new BizA();
    }
    public IBiz createBizB() {
        return new BizB();
    }
}
```

## 3. 静态工厂模式

```java
public class BizFactory {
    public static IBiz createBizA() {
        return new BizA();
    }
    public static IBiz createBizB() {
        return new BizB();
    }
}
```
