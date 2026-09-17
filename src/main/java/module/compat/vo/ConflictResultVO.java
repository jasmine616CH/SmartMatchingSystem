package module.compat.vo;

import java.util.List;

import lombok.Data;

/**
 * 方案兼容性冲突检测结果
 * <p>
 * 三种情况分开计数，前端可区分「真冲突」与「数据不全没法判」：
 * <ul>
 *   <li>冲突（不满足）—— 硬性规则未通过，计入 {@code hardConflictCount}</li>
 *   <li>偏差（临界）—— 偏好规则未通过，计入 {@code softConflictCount}</li>
 *   <li>未判定（缺失）—— 任一侧缺少规则声明的参数，计入 {@code undeterminedCount}</li>
 * </ul>
 * 未判定<b>不计为冲突</b>：缺参数时 Aviator 会静默返回 false，
 * 若不做前置判断就会产出假冲突。
 */
@Data
public class ConflictResultVO {

    /** 方案主键ID */
    private Long schemeId;

    /** 是否兼容：无硬性冲突即视为兼容 */
    private Boolean compatible;

    /** 硬性冲突条数 */
    private Integer hardConflictCount;

    /** 偏好偏差条数 */
    private Integer softConflictCount;

    /** 未判定条数（两侧参数不全，无法比较） */
    private Integer undeterminedCount;

    /** 参与检测的规则总数 */
    private Integer ruleCount;

    /** 命中的冲突与偏差明细 */
    private List<ConflictItemVO> conflicts;
}
