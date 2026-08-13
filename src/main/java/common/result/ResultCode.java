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

    ;

    private Integer code;
    private String massage;

    private ResultCode(Integer code, String massage) {
        this.code = code;
        this.massage = massage;
    }

}
