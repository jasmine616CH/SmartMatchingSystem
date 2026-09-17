package module.price.vo;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 配件解析后的唯一单价
 * <p>
 * 一个配件可能有多家供应商、多条报价，本 VO 表示按业务优先级挑出的那一条。
 * 解析规则见 {@code PartSupplierPriceMapper.selectUnitPriceByPartIds}。
 * <p>
 * 查不到报价的配件<b>不会出现在结果里</b>，调用方据此判定「暂无报价」。
 */
@Data
public class PartUnitPriceVO {

    /** 配件主键ID */
    private Long partId;

    /** 报价主键ID */
    private Long priceId;

    /** 外键：part_supplier.ps_id */
    private Long psId;

    /** 供应商主键ID */
    private Long supplierId;

    /** 供应商企业全称 */
    private String supplierName;

    /** 供货类型：main-主供 spare-备供 */
    private String supplyType;

    /** 价格类型：standard-标准价 agreement-协议框架价 */
    private String priceType;

    /** 计价币种 */
    private String currency;

    /** 单品单价 */
    private BigDecimal priceValue;

    /** 最小起订量MOQ */
    private Integer moq;

    /** 交付周期（天） */
    private Integer leadTime;
}
