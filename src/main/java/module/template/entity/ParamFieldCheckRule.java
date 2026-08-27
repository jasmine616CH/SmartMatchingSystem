package module.template.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("param_field_check_rule")
public class ParamFieldCheckRule {
    /**
     * 主键雪花ID
     */
    @TableId
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
     * 和上一个校验组的关系：AND并且 / OR或者，第一条忽略该字段
     */
    private String joinLogic;

    /**
     * 0禁用 1启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
