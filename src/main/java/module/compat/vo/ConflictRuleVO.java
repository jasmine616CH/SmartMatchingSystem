package module.compat.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 冲突规则
 */
@Data
public class ConflictRuleVO {

    /** 规则主键ID */
    private Long ruleId;

    /** 规则名称 */
    private String ruleName;

    /** A侧二级子系统ID */
    private Long catAId;

    /** A侧二级子系统名称 */
    private String catAName;

    /** B侧二级子系统ID */
    private Long catBId;

    /** B侧二级子系统名称 */
    private String catBName;

    /** A侧参数编码 */
    private String paramCodeA;

    /** B侧参数编码 */
    private String paramCodeB;

    /** Aviator 表达式 */
    private String checkExpr;

    /** 约束强度：0-硬性冲突 1-偏好偏差 */
    private Integer severity;

    /** 约束强度中文描述 */
    private String severityName;

    /** 冲突描述 */
    private String errorMsg;

    /** 整改建议 */
    private String solveSuggest;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    /** 执行顺序 */
    private Integer sort;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
