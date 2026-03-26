 我来为你详细对比 Spring 和 Vert.x 两大框架体系的异常处理机制，并提供优雅的实践方案。

---

## 一、Spring 框架体系的异常处理

### 1. 全局异常处理（推荐方式）

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Result.fail(e.getCode(), e.getMessage()));
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Result.fail(ErrorCode.PARAM_ERROR, message));
    }

    // 资源未找到
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNotFound(NoHandlerFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Result.fail(ErrorCode.NOT_FOUND, "资源不存在"));
    }

    // 兜底异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Result.fail(ErrorCode.SYSTEM_ERROR, "系统繁忙，请稍后重试"));
    }
}
```

### 2. 分层异常体系设计

```java
// 基础异常
public abstract class BaseException extends RuntimeException {
    private final String code;
    
    protected BaseException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    protected BaseException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public String getCode() { return code; }
}

// 业务异常
public class BusinessException extends BaseException {
    public BusinessException(String message) {
        super(ErrorCode.BUSINESS_ERROR, message);
    }
    
    public BusinessException(String code, String message) {
        super(code, message);
    }
}

// 具体业务场景异常
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long userId) {
        super(ErrorCode.USER_NOT_FOUND, "用户不存在: " + userId);
    }
}
```

### 3. 错误码枚举

```java
public interface ErrorCode {
    String SUCCESS = "00000";
    String SYSTEM_ERROR = "B0001";
    String PARAM_ERROR = "A0400";
    String USER_NOT_FOUND = "A0201";
    String UNAUTHORIZED = "A0301";
    String FORBIDDEN = "A0302";
}

public enum ErrorCodeEnum implements ErrorCode {
    SUCCESS("00000", "成功"),
    SYSTEM_ERROR("B0001", "系统执行出错"),
    PARAM_ERROR("A0400", "请求参数错误"),
    USER_NOT_FOUND("A0201", "用户不存在"),
    UNAUTHORIZED("A0301", "访问未授权"),
    FORBIDDEN("A0302", "禁止访问");
    
    private final String code;
    private final String message;
    
    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    // getters...
}
```

### 4. 统一响应包装

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private String code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS, "success", data, System.currentTimeMillis());
    }
    
    public static <T> Result<T> fail(String code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }
}
```

### 5. 函数式异常处理工具

```java
@Component
public class ExceptionUtils {
    
    // 包装受检异常为运行时异常
    public static <T> T wrap(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage(), e);
        }
    }
    
    // 可选值处理
    public static <T> Optional<T> optional(Callable<T> callable) {
        try {
            return Optional.ofNullable(callable.call());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    // 带降级逻辑的函数
    public static <T> T withFallback(Callable<T> primary, Supplier<T> fallback) {
        try {
            return primary.call();
        } catch (Exception e) {
            return fallback.get();
        }
    }
}
```

---

## 二、Vert.x 框架体系的异常处理

Vert.x 是**异步非阻塞**框架，异常处理模式与 Spring 完全不同。

### 1. 异步异常处理核心模式

```java
public class UserService {
    
    private final Vertx vertx;
    private final WebClient client;
    
    public Future<User> getUser(Long id) {
        // Future 链式异常传播
        return fetchFromCache(id)
            .recover(throwable -> fetchFromDatabase(id))  // 降级处理
            .recover(throwable -> fetchFromRemote(id))
            .onSuccess(user -> log.info("获取用户成功: {}", user.getName()))
            .onFailure(throwable -> log.error("获取用户失败", throwable));
    }
    
    // 带超时的异步操作
    public Future<User> getUserWithTimeout(Long id) {
        return getUser(id)
            .compose(user -> {
                if (user == null) {
                    return Future.failedFuture(new UserNotFoundException(id));
                }
                return Future.succeededFuture(user);
            })
            .timeout(3000, TimeUnit.MILLISECONDS);  // 超时控制
    }
}
```

### 2. Vert.x Web 全局异常处理

```java
public class GlobalFailureHandler implements Handler<RoutingContext> {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalFailureHandler.class);
    
    @Override
    public void handle(RoutingContext ctx) {
        Throwable failure = ctx.failure();
        int statusCode = ctx.statusCode() != -1 ? ctx.statusCode() : 500;
        
        // 处理特定异常类型
        if (failure instanceof ValidationException) {
            handleValidationException(ctx, (ValidationException) failure);
        } else if (failure instanceof ServiceException) {
            handleServiceException(ctx, (ServiceException) failure);
        } else if (failure instanceof TimeoutException) {
            handleTimeoutException(ctx);
        } else {
            handleUnknownException(ctx, failure);
        }
    }
    
    private void handleValidationException(RoutingContext ctx, ValidationException e) {
        JsonObject error = new JsonObject()
            .put("code", "A0400")
            .put("message", "参数校验失败: " + e.getMessage());
        
        ctx.response()
            .setStatusCode(400)
            .putHeader("Content-Type", "application/json")
            .end(error.encode());
    }
    
    private void handleUnknownException(RoutingContext ctx, Throwable e) {
        log.error("未处理的异常", e);
        
        JsonObject error = new JsonObject()
            .put("code", "B0001")
            .put("message", "系统繁忙，请稍后重试");
        
        ctx.response()
            .setStatusCode(500)
            .putHeader("Content-Type", "application/json")
            .end(error.encode());
    }
}

// 注册路由
router.route().failureHandler(new GlobalFailureHandler());
```

### 3. EventBus 异常处理

```java
public class EventBusExample {
    
    public void sendWithRetry() {
        DeliveryOptions options = new DeliveryOptions()
            .setSendTimeout(5000)
            .setCodecName("json");
        
        vertx.eventBus().request("service.user.get", new JsonObject().put("id", 1), options, ar -> {
            if (ar.succeeded()) {
                // 处理成功
                Message<JsonObject> message = ar.result();
                processResponse(message.body());
            } else {
                // 统一异常处理
                handleEventBusFailure(ar.cause());
            }
        });
    }
    
    private void handleEventBusFailure(Throwable cause) {
        if (cause instanceof ReplyException) {
            ReplyException re = (ReplyException) cause;
            switch (re.failureCode()) {
                case ReplyFailure.NO_HANDLERS:
                    log.error("无可用服务处理器");
                    break;
                case ReplyFailure.TIMEOUT:
                    log.error("服务调用超时");
                    break;
                case ReplyFailure.RECIPIENT_FAILURE:
                    log.error("服务执行失败: {}", re.getMessage());
                    break;
            }
        }
    }
}
```

### 4. 异步工具类封装

```java
public class AsyncUtils {
    
    // 将 CompletableFuture 转换为 Vert.x Future
    public static <T> Future<T> toFuture(CompletableFuture<T> future) {
        Promise<T> promise = Promise.promise();
        future.whenComplete((result, error) -> {
            if (error != null) {
                promise.fail(error);
            } else {
                promise.complete(result);
            }
        });
        return promise.future();
    }
    
    // 批量执行并优雅处理部分失败
    public static <T> Future<List<T>> allOrSome(List<Future<T>> futures) {
        return CompositeFuture.all(futures)
            .map(cf -> futures.stream()
                .map(f -> f.result())
                .collect(Collectors.toList()))
            .recover(throwable -> {
                // 部分成功时返回成功的结果
                return Future.succeededFuture(
                    futures.stream()
                        .filter(Future::succeeded)
                        .map(Future::result)
                        .collect(Collectors.toList())
                );
            });
    }
    
    // 带指数退避的重试机制
    public static <T> Future<T> retryWithBackoff(Supplier<Future<T>> supplier, int maxRetries) {
        return retryWithBackoff(supplier, maxRetries, 100);
    }
    
    private static <T> Future<T> retryWithBackoff(Supplier<Future<T>> supplier, int retries, long delay) {
        return supplier.get().recover(err -> {
            if (retries <= 0) {
                return Future.failedFuture(err);
            }
            Promise<T> promise = Promise.promise();
            vertx.setTimer(delay, id -> {
                retryWithBackoff(supplier, retries - 1, delay * 2)
                    .onComplete(promise::handle);
            });
            return promise.future();
        });
    }
}
```

---

## 三、两大框架对比总结

| 维度 | Spring Framework | Vert.x |
|------|------------------|--------|
| **编程模型** | 同步/阻塞（也可异步） | 异步/非阻塞 |
| **异常传播** | 栈展开，try-catch | Future 链式传播 |
| **全局处理** | `@ControllerAdvice` | `FailureHandler` |
| **降级策略** | AOP / Hystrix | `recover()` 操作符 |
| **超时控制** | `@TimeLimiter` / 注解 | `timeout()` 方法 |
| **重试机制** | Spring Retry | 自定义 Future 链 |
| **线程安全** | 需考虑线程上下文 | 单线程 EventLoop，天然安全 |

---

## 四、最佳实践建议

### Spring 场景
1. **分层异常**：区分系统异常、业务异常、第三方异常
2. **统一入口**：所有异常最终都经过 `@ControllerAdvice`
3. **日志分级**：业务异常 warn，系统异常 error
4. **敏感信息**：生产环境隐藏堆栈信息

### Vert.x 场景
1. **始终处理**：每个 Future 都要有 `onFailure` 或 `recover`
2. **超时必设**：所有外部调用设置超时
3. **断路器**：使用 `CircuitBreaker` 防止级联故障
4. **上下文传递**：使用 `Context` 保持请求追踪
