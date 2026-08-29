package module.template.vo;

import lombok.Data;

@Data
public class ParamFieldCheckRuleVO {
    /**
     * 主键雪花ID
     */
    private Long checkRuleId;

    /**
     * 关联param_template_field.field_id，模板参数字段ID
     */
    private Long fieldId;

    /**
     * 规则名称，如：额定功率大于0
     */
    private String ruleName;

    /**
     * Aviator校验表达式，true合法 false非法
     */
    private String checkExpr;

    /**
     * 该规则不通过时的错误提示
     */
    private String errorMsg;

    /**
     * 校验执行顺序
     */
    private Integer sort;

    /**
     * 0禁用 1启用
     */
    private Integer status;
}
