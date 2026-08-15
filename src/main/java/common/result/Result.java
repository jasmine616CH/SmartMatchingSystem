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
     */
    public static <T> Result<T> error(BusinessException e) {
        ResultCode resultCode = e.getResultCode();
        if (resultCode != null) {
            return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
        } else {
            // 降级处理：使用异常自带消息
            return new Result<>(500, e.getMessage(), null);
        }
    }
}