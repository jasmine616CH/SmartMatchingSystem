package module.price.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 配件供应商报价表（版本化管理） (part_supplier_price)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartSupplierPrice {

    /** 报价主键ID（雪花算法，业务生成） */
    private Long priceId;

    /** 外键：part_supplier.ps_id 配件供应商关联主键 */
    private Long psId;

    /** 价格类型：standard-标准价 agreement-协议框架价 */
    private String priceType;

    /** 计价币种，默认人民币 */
    private String currency;

    /** 单品单价 */
    private BigDecimal priceValue;

    /** 最小起订量MOQ */
    private Integer moq;

    /** 交付周期（天） */
    private Integer leadTime;

    /** 报价生效日期 */
    private LocalDate effectDate;

    /** 报价失效日期，为空永久有效 */
    private LocalDate expireDate;

    /** 报价录入操作人员ID */
    private Long createUserId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
