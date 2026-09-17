package module.scheme.vo;

import java.util.List;

import lombok.Data;

/**
 * 方案参数校验结果
 * <p>
 * 判定口径：
 * <ul>
 *   <li>{@code pass} = 无「不满足」且无「缺失」，即硬性约束全部通过且必填参数齐全</li>
 *   <li>偏好条件不通过只计入 {@code softFailCount}（临界），不影响 pass</li>
 * </ul>
 * 注意 {@code ruleCount} 为 0 时应显示「无可校验规则」而非「校验通过」——
 * 没有任何规则参与校验与全部通过是两回事。
 */
@Data
public class SchemeValidateResultVO {

    /** 是否通过：无硬性约束不满足项，且无必填参数缺失 */
    private Boolean pass;

    /** 参与校验的启用规则总数 */
    private Integer ruleCount;

    /** 不满足项总数（硬性约束未通过） */
    private Integer failCount;

    /** 临界项总数（偏好条件未通过） */
    private Integer softFailCount;

    /** 缺失项总数（必填参数未录入） */
    private Integer missingCount;

    /** 有问题的配件明细，全部满足时为空数组 */
    private List<SchemePartValidateVO> results;
}
