package module.scheme.vo;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 方案汇总：改数量 / 删明细后直接返回，前端无需再拉一次详情
 */
@Data
public class SchemeSummaryVO {

    /** 配件数量 */
    private Integer partCount;

    /** 方案总价 */
    private BigDecimal totalPrice;
}
