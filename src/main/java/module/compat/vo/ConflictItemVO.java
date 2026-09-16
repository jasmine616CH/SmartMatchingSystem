package module.compat.vo;

import lombok.Data;

/**
 * 一条命中的兼容性冲突
 */
@Data
public class ConflictItemVO {

    /** 命中的规则ID */
    private Long ruleId;

    /** 规则名称 */
    private String ruleName;

    /** A侧配件ID */
    private Long partAId;

    /** A侧物料编码 */
    private String partACode;

    /** A侧配件展示名 */
    private String partAName;

    /** A侧参与比较的参数编码 */
    private String paramCodeA;

    /** A侧实际取值 */
    private String valueA;

    /** B侧配件ID */
    private Long partBId;

    /** B侧物料编码 */
    private String partBCode;

    /** B侧配件展示名 */
    private String partBName;

    /** B侧参与比较的参数编码 */
    private String paramCodeB;

    /** B侧实际取值 */
    private String valueB;

    /** 冲突涉及的参数名称（取「A侧编码 ↔ B侧编码」） */
    private String conflictParam;

    /** 冲突详细描述 */
    private String conflictDesc;

    /** 整改建议 */
    private String solveSuggest;

    /** 约束强度：0-硬性冲突 1-偏好偏差 */
    private Integer severity;

    /** 约束强度中文描述 */
    private String severityName;
}
