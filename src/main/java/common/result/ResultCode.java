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

    /**
     * 用户模块
     */
    USER_NOT_FOUND(40401, "用户不存在"),
    PASSWORD_ERROR(40402, "密码错误"),
    USER_NOT_LOGIN(40403, "用户未注册"),

    /**
     * token模块
     */
    TOKEN_EXPIRED(40001 , "Token已过期"),
    TOKEN_SIGNATURE_ERROR(40002 , "Token签名异常"),
    TOKEN_MALFORMED(40003 , "Token格式不对"),
    TOKEN_INVALID(40004 , "Token无效"),

    ;

    private Integer code;
    private String massage;

    private ResultCode(Integer code, String massage) {
        this.code = code;
        this.massage = massage;
    }

}
