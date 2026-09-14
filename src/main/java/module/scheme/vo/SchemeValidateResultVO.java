package module.scheme.vo;

import java.util.List;

import lombok.Data;

/**
 * 方案参数校验结果
 * <p>
 * 注意 ruleCount 为 0 时应显示「无可校验规则」而非「校验通过」——
 * 没有任何规则参与校验与全部通过是两回事。
 */
@Data
public class SchemeValidateResultVO {

    /** 是否全部通过 */
    private Boolean pass;

    /** 参与校验的启用规则总数 */
    private Integer ruleCount;

    /** 未通过的规则总数 */
    private Integer failCount;

    /** 未通过的配件明细，全部通过时为空数组 */
    private List<SchemePartValidateVO> results;
}
