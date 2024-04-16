# 

[SpringBoot 2.4+ profile](https://www.cnblogs.com/xysn/p/14805281.html)

## 2.4之前的的yml文件配置如下：

```yml
spring: 
  profiles:
    active: test
```

## 2.4版本之后修改为：

```yml
spring: 
#  profiles:
#    active: test
  config:
    activate:
      on-profile:
      - test
```
