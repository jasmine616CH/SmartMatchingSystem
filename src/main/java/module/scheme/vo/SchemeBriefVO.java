package module.scheme.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 方案列表行
 */
@Data
public class SchemeBriefVO {

    /** 方案主键ID */
    private Long schemeId;

    /** 方案名称 */
    private String schemeName;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 配件数量（scheme_part 行数） */
    private Integer partCount;

    /** 方案总价（单价 × 数量 求和，不含无报价配件） */
    private BigDecimal totalPrice;

    /** 是否存在正式 BOM 配件 */
    private Boolean isSelectBom;
}
