package module.scheme.vo;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * 单个配件的参数校验结果
 * <p>
 * 四种判定态分别落在三个列表里：
 * <ul>
 *   <li>不满足 —— {@link #failItems}，硬性约束未通过，该配件不应被采用</li>
 *   <li>临界 —— {@link #warnItems}，偏好条件未通过，不影响可用性但会拉低匹配度</li>
 *   <li>缺失 —— {@link #missingItems}，必填参数没有录入值</li>
 * </ul>
 * 三者皆空即该配件所有参数均为「满足」。
 */
@Data
public class SchemePartValidateVO {

    /** 明细主键ID */
    private Long schemePartId;

    /** 配件主键ID */
    private Long partId;

    /** 物料编码 */
    private String partCode;

    /** 配件展示名称 */
    private String partName;

    /** 未通过硬性约束的项（不满足） */
    private List<ValidateFailItemVO> failItems;

    /** 未通过偏好条件的项（临界） */
    private List<ValidateFailItemVO> warnItems;

    /** 必填但未录入的参数（缺失） */
    private List<MissingParamVO> missingItems;

    /**
     * 参数匹配度（0-100，保留2位小数）
     * <p>= (通过校验的偏好条件数 / 偏好条件总数) × 100。
     * 该配件没有任何偏好条件时为 null，表示「无可比口径」而非满分为 0。
     */
    private BigDecimal matchScore;
}
