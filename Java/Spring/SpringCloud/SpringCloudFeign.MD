# Spring Cloud Feign

## token 传递 & header 传递

```java

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 从header 获取 token
        log.info("request: {}", template.url());
        if (servletRequestAttributes != null) {
            template.header("Authorization", servletRequestAttributes.getRequest().getHeader("Authorization"));
        }
    }
}
```

## 子线程 header 传递



## 打印请求响应日志

* [全局记录Feign的请求和响应日志](https://blog.csdn.net/youbl/article/details/109047987)
