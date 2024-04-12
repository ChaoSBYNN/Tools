#

```java
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * @ClassName BaseResponse
 * @Description 通用返回对象
 * 1. 默认状态
 *      BaseResponse.success(data)
 *      BaseResponse.failed()
 *      BaseResponse.forbidden()
 *      BaseResponse.unauthorized()
 * 2. 默认状态 + 自定义提示
 *      BaseResponse.failed(message)
 *      BaseResponse.forbidden(message)
 *      BaseResponse.unauthorized(message)
 * 2. 默认状态 + 自定义状态
 *      BaseResponse.failed(code)
 *      BaseResponse.forbidden(code)
 *      BaseResponse.unauthorized(code)
 * 3. 默认状态 + 自定义状态 + 自定义提示
 *      BaseResponse.failed(code, message)
 *      BaseResponse.forbidden(code, message)
 *      BaseResponse.unauthorized(code, message)
 * @Author Spike_Zhang
 * @DATE 2024/4/7 9:39
 * @Version 1.0
 */
@Getter
@Setter
public class BaseResponse<T> {
    /**
     * 自定义状态码
     */
    private long code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 默认成功返回结果
     * @param code
     * @param message
     * @param data
     */
    protected BaseResponse(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(ResultCode.OK.getCode(), ResultCode.OK.getMsg(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<T>(ResultCode.OK.getCode(), message, data);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static <T> BaseResponse<T> failed(ErrorCode errorCode) {
        return new BaseResponse<T>(errorCode.getResultCode().getCode(), errorCode.getErrDes(), null);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static <T> BaseResponse<T> failed(ErrorCode errorCode, String message) {
        return new BaseResponse<T>(errorCode.getResultCode().getCode(), message, null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> BaseResponse<T> failed(String message) {
        return new BaseResponse<T>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> BaseResponse<T> failed() {
        return failed(ResultCode.INTERNAL_SERVER_ERROR.getMsg());
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> BaseResponse<T> validateFailed() {
        return failed(ResultCode.NOT_FOUND.getMsg());
    }

    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static <T> BaseResponse<T> validateFailed(String message) {
        return new BaseResponse<T>(ResultCode.NOT_FOUND.getCode(), message, null);
    }


    /**
     * 未登录返回结果
     */
    public static <T> BaseResponse<T> unauthorized() {
        return new BaseResponse<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMsg(), null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> BaseResponse<T> unauthorized(T data) {
        return new BaseResponse<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMsg(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> BaseResponse<T> forbidden(T data) {
        return new BaseResponse<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMsg(), data);
    }

}
```


```java

public enum DefaultErrorCode implements ErrorCode {

  /** 用户名或密码不匹配 */
  ACCOUNT_PASSWORD_NOT_MATCH(ResultCode.BAD_REQUEST, "account_password_not_match", "用户名或密码不匹配"),
  /** 内部服务器错误 */
  INTERNAL_SERVER_ERROR(ResultCode.INTERNAL_SERVER_ERROR, "internal_server_error", "内部服务器错误"),
  /** 未知错误 */
  UNKNOWN_ERROR(ResultCode.INTERNAL_SERVER_ERROR, "unknown_error", "未知错误"),
  /** 脏数据 */
  DIRTY_DATA(ResultCode.INTERNAL_SERVER_ERROR, "dirty_data", "脏数据"),
  /** 非法参数 */
  INVALID_PARAMETER(ResultCode.BAD_REQUEST, "invalid_parameter", "非法参数"),
  /** 缺少路径参数 */
  MISSING_PATH_VARIABLE(ResultCode.BAD_REQUEST, "missing_path_variable", "缺少路径参数"),
  /** 缺少请求头 */
  MISSING_REQUEST_HEADER(ResultCode.BAD_REQUEST, "missing_request_header", "缺少请求头"),
  /** 401 */
  UNAUTHORIZED(ResultCode.UNAUTHORIZED, "unauthorized", "无权限访问"),
  /** 请求未找到 */
  REQUEST_NOT_FOUND(ResultCode.NOT_FOUND, "request_not_found", "请求未找到"),
  /** 请求方法不支持 */
  METHOD_NOT_ALLOWED(ResultCode.METHOD_NOT_ALLOWED, "method_not_allowed", "请求方法不支持"),
  ;

  @Getter
  private final ResultCode resultCode;
  @Getter
  private final String errCode;
  @Getter
  private final String errDes;

  DefaultErrorCode(ResultCode resultCode, String errCode, String errDes) {
    this.resultCode = resultCode;
    this.errCode = errCode;
    this.errDes = errDes;
  }
}

```

```java
public interface ErrorCode extends Serializable {

  ResultCode getResultCode();

  String getErrCode();

  String getErrDes();
}
```

```java
@Data
public class ErrorResult implements Serializable {
  private static final long serialVersionUID = 4743989351981430858L;
  protected int code;
  protected int httpStatus;
  protected String msg;
  protected String errCode;
  protected String errDes;
  protected Object[] i18nArgs;

  public ErrorResult() {}

  public ErrorResult(ErrorCode errorCode) {
    this.code = errorCode.getResultCode().getCode();
    this.msg = errorCode.getResultCode().getMsg();
    this.errCode = errorCode.getErrCode();
    this.errDes = errorCode.getErrDes();
  }

  public ErrorResult(ErrorCode errorCode, Object[] i18nArg) {
    this.code = errorCode.getResultCode().getCode();
    this.msg = errorCode.getResultCode().getMsg();
    this.errCode = errorCode.getErrCode();
    this.errDes = errorCode.getErrDes();
    this.i18nArgs = i18nArg;
  }

  public ErrorResult httpStatus(int httpStatus) {
    this.httpStatus = httpStatus;
    return this;
  }

  public int getHttpStatus() {
    if (httpStatus == 0) {
      return code;
    }
    return httpStatus;
  }
}
```
