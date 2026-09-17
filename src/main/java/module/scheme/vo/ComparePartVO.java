package module.scheme.vo;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 并排比较的列头：一个候选配件
 */
@Data
public class ComparePartVO {

    /** 配件主键ID */
    private Long partId;

    /** 物料编码 */
    private String partCode;

    /** 配件展示名（分类名 + 型号） */
    private String partName;

    /** 品牌 */
    private String brand;

    /** 型号 */
    private String model;

    /** 是否有可用报价 */
    private Boolean hasPrice;

    /** 解析后的单价，无报价时为 null */
    private BigDecimal unitPrice;

    /** 计价币种，固定人民币 */
    private String currency;

    /** 该配件在本轮比对中的不满足项数 */
    private Integer violatedCount;

    /** 该配件的临界项数 */
    private Integer warningCount;

    /** 该配件的缺失项数 */
    private Integer missingCount;
}
