package module.scheme.vo;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 方案配件明细行
 */
@Data
public class SchemePartVO {

    /** 明细主键ID */
    private Long schemePartId;

    /** 配件主键ID */
    private Long partId;

    /** 物料编码 */
    private String partCode;

    /** 配件展示名称，由「分类名 + 型号」派生，表中无此列 */
    private String partName;

    /** 三级分类ID */
    private Long catId;

    /** 三级分类名称 */
    private String catName;

    /** 一级系统名称（由三级分类回溯得出） */
    private String systemName;

    /** 配件品牌 */
    private String brand;

    /** 配件型号 */
    private String model;

    /** 选用数量 */
    private Integer quantity;

    /** 解析后的单价；无报价时为 null */
    private BigDecimal unitPrice;

    /** 小计（单价 × 数量）；无报价时为 null */
    private BigDecimal subtotal;

    /** 计价币种 */
    private String currency;

    /** 供应商名称 */
    private String supplierName;

    /** 最小起订量 */
    private Integer moq;

    /** 交付周期（天） */
    private Integer leadTime;

    /** 匹配度得分 */
    private BigDecimal matchScore;

    /** 是否纳入最终BOM：0-候选 1-正式BOM */
    private Integer isSelectBom;

    /** 是否有可用报价，前端据此显示「暂无报价」 */
    private Boolean hasPrice;
}
