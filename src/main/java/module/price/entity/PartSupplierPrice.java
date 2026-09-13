package module.price.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

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
    @TableId
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

    /**
     * 报价失效日期，为空永久有效
     * <p>
     * 更新策略固定为 ALWAYS：该字段是唯一可空的业务字段，默认的 NOT_NULL 策略会把
     * 「传 null 表示永久有效」静默忽略，导致已设失效日期的报价永远无法改回永久有效。
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate expireDate;

    /** 报价录入操作人员ID */
    private Long createUserId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
