package module.scheme.vo;

import lombok.Data;

/**
 * 参数校验失败项
 */
@Data
public class ValidateFailItemVO {

    /** 模板参数字段ID */
    private Long fieldId;

    /** 参数中文名 */
    private String paramCn;

    /** 校验规则ID */
    private Long checkRuleId;

    /** 规则名称 */
    private String ruleName;

    /** 校验表达式原文 */
    private String checkExpr;

    /** 配件该参数的实际取值 */
    private String actualValue;

    /** 规则不通过时的错误提示 */
    private String errorMsg;
}
