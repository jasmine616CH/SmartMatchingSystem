package module.compat.vo;

import lombok.Data;

/**
 * 表达式语法校验结果
 */
@Data
public class ExprValidateVO {

    /** 语法是否合法 */
    private Boolean valid;

    /** 不合法时的错误信息，合法时为 null */
    private String message;
}
