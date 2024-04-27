# Constant 常量

## 获取类中全部常量列表

```java
    public static List<String> getConstantsList(Class<?> clazz) {
        Field[] fields = clazz.getFields();
        List<String> constants = new ArrayList<>();
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                try {
                    constants.add((String) field.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return constants;
    }
```
