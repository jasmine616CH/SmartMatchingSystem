package common.result;

import common.exception.BusinessException;
import lombok.Data;

/**
 * 统一响应结果
 */
@Data
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        return new Result<>(1, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(1, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(1, message, data);
    }

    // ========== 错误响应 ==========
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(0, message, null);
    }

    /**
     *  基于 ResultCode 枚举
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 基于 BusinessException
     * <p>
     * 消息取异常自身的 message 而不是枚举的固定文案：
     * BusinessException(ResultCode) 的构造器已把 message 设为枚举文案，
     * 而 BusinessException(ResultCode, String) 带的是调用方写的具体原因
     * （如「统一社会信用代码【xxx】已存在」）。若这里取枚举文案，后者会被静默丢弃。
     */
    public static <T> Result<T> error(BusinessException e) {
        ResultCode resultCode = e.getResultCode();
        if (resultCode == null) {
            return new Result<>(500, e.getMessage(), null);
        }
        String message = e.getMessage();
        return new Result<>(resultCode.getCode(),
                message == null || message.isEmpty() ? resultCode.getMessage() : message,
                null);
    }
}