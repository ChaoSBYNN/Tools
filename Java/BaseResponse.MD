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

```java

import lombok.Getter;
import org.springframework.lang.Nullable;

public enum ResultCode {
  // 2xx Success

  /** 成功了。服务器已成功处理了请求。 */
  OK(200, Series.SUCCESSFUL, "OK"),
  /** 资源已创建。请求成功并且服务器创建了新的资源。 */
  CREATED(201, Series.SUCCESSFUL, "Created"),
  /** 已接受但未处理。服务器已接受请求，但尚未处理。 */
  ACCEPTED(202, Series.SUCCESSFUL, "Accepted"),
  // --- 4xx Client Error ---

  /** 参数错误 */
  BAD_REQUEST(400, Series.CLIENT_ERROR, "Bad Request"),
  /** 未认证， 未授权 */
  UNAUTHORIZED(401, Series.CLIENT_ERROR, "Unauthorized"),
  /** 需付费 */
  PAYMENT_REQUIRED(402, Series.CLIENT_ERROR, "Payment Required"),
  /** 拒绝访问 */
  FORBIDDEN(403, Series.CLIENT_ERROR, "Forbidden"),
  /** 未找到 */
  NOT_FOUND(404, Series.CLIENT_ERROR, "Not Found"),
  /** 方法不被允许 */
  METHOD_NOT_ALLOWED(405, Series.CLIENT_ERROR, "Method Not Allowed"),
  /** 请求超时 */
  REQUEST_TIMEOUT(408, Series.CLIENT_ERROR, "Request Timeout"),
  /** 请求Body过大 */
  PAYLOAD_TOO_LARGE(413, Series.CLIENT_ERROR, "Payload Too Large"),
  /** 请求URL过长 */
  URI_TOO_LONG(414, Series.CLIENT_ERROR, "URI Too Long"),
  /** 绿茶警告 */
  I_AM_A_TEAPOT(418, Series.CLIENT_ERROR, "I'm a teapot"),
  /** 请求次数过多 */
  TOO_MANY_REQUESTS(429, Series.CLIENT_ERROR, "Too Many Requests"),
  /** 请求头过大 */
  REQUEST_HEADER_FIELDS_TOO_LARGE(431, Series.CLIENT_ERROR, "Request Header Fields Too Large"),

  // --- 5xx Server Error ---

  /** 内部服务器错误 */
  INTERNAL_SERVER_ERROR(500, Series.SERVER_ERROR, "Internal Server Error"),
  /** 功能未实现 */
  NOT_IMPLEMENTED(501, Series.SERVER_ERROR, "Not Implemented"),
  /** 网关错误 */
  BAD_GATEWAY(502, Series.SERVER_ERROR, "Bad Gateway"),
  /** 服务不可用 */
  SERVICE_UNAVAILABLE(503, Series.SERVER_ERROR, "Service Unavailable"),
  /** 网关超时 */
  GATEWAY_TIMEOUT(504, Series.SERVER_ERROR, "Gateway Timeout"),

  // --- xxx Custom ---

  ;
  @Getter
  private final int code;
  @Getter
  private final Series series;
  @Getter
  private final String msg;

  ResultCode(int code, Series series, String msg) {
    this.code = code;
    this.series = series;
    this.msg = msg;
  }


  public enum Series {

    /** 成功状态类型 */
    SUCCESSFUL(2),
    CUSTOM(3),
    /** 客户端错误类型 */
    CLIENT_ERROR(4),
    /** 服务端错误类型 */
    SERVER_ERROR(5);

    private final int value;

    Series(int value) {
      this.value = value;
    }

    /**
     * Return the integer value of this status series. Ranges from 1 to 5.
     */
    public int value() {
      return this.value;
    }


    /**
     * Return the {@code Series} enum constant for the supplied status code.
     *
     * @param statusCode the HTTP status code (potentially non-standard)
     * @return the {@code Series} enum constant for the supplied status code
     * @throws IllegalArgumentException if this enum has no corresponding constant
     */
    public static Series valueOf(int statusCode) {
      Series series = resolve(statusCode);
      if (series == null) {
        throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
      }
      return series;
    }

    /**
     * Resolve the given status code to an {@code Series}, if possible.
     *
     * @param statusCode the HTTP status code (potentially non-standard)
     * @return the corresponding {@code Series}, or {@code null} if not found
     * @since 5.1.3
     */
    @Nullable
    public static Series resolve(int statusCode) {
      int seriesCode = statusCode / 100;
      for (Series series : values()) {
        if (series.value == seriesCode) {
          return series;
        }
      }
      return null;
    }
  }
}

```
