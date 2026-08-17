package common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    /**
     * 状态码
     */
    SUCCESS(1, "success"),
    ERROR(0, "error"),

    /** 参数校验失败 */
    PARAM_VALID_FAIL(400, "参数校验失败"),
    /** 未授权 */
    UNAUTHORIZED(401, "未授权，请先登录"),
    /** 无权限 */
    FORBIDDEN(403, "无权限访问"),
    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),
    /** 请求方法不允许 */
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    /** 系统内部异常 */
    INTERNAL_ERROR(500, "系统内部异常"),

    // ==================== 参数相关业务码 ====================
    PARAM_IS_NULL(40000, "必填参数不能为空"),
    PARAM_FORMAT_ERROR(40007, "参数格式错误"),
    PARAM_VALUE_INVALID(40008, "参数取值非法"),
    PARAM_DUPLICATE(40009, "参数数据重复"),
    PARAM_OVER_LENGTH(40010, "参数长度超出限制"),
    PARAM_RANGE_ERROR(40011, "参数数值超出合法范围"),

    /**
     * 用户模块
     */
    USER_NOT_FOUND(40401, "用户不存在"),
    PASSWORD_ERROR(40402, "密码错误"),
    USER_NOT_LOGIN(40403, "用户未注册"),
    USER_LOGOUT_FAIL(40404, "登出失败"),
    USER_ALREADY_EXISTS(40405, "该手机号已注册，请勿重复注册"),

    /**
     * token模块
     */
    TOKEN_EXPIRED(40001, "Token已过期"),
    TOKEN_SIGNATURE_ERROR(40002, "Token签名异常"),
    TOKEN_MALFORMED(40003, "Token格式不对"),
    TOKEN_INVALID(40004, "Token无效"),
    TOKEN_NOT_REFRESH_(40005, "Token刷新失败"),
    TOKEN_FAIL_REFRESH(40006, "Token刷新异常"),

    /** 数据不存在 */
    DATA_NOT_EXIST(42201, "数据不存在"),
    DATA_DUPLICATE(40009, "数据重复"),
    /** 当前状态不允许执行该操作 */
    STATUS_OPERATE_FORBIDDEN(42203, "当前状态不允许执行该操作");

    private Integer code;
    private String message;

    private ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
