package module.price.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 修改配件供应商报价入参
 * <p>
 * 字段与新增一致，仅多出主键 priceId。全量覆盖式更新，不做部分字段补丁。
 * 报价录入人 createUserId 不接受前端传入，修改时保持原值不变。
 */
@Data
public class PartSupplierPriceUpdateDTO {

    /** 报价主键ID */
    @NotNull(message = "priceId 不能为空")
    private Long priceId;

    /** 外键：part_supplier.ps_id 配件供应商关联主键 */
    @NotNull(message = "psId 不能为空")
    private Long psId;

    /** 价格类型：standard-标准价 agreement-协议框架价 */
    @NotBlank(message = "priceType 不能为空")
    @Pattern(regexp = "^(standard|agreement)$", message = "priceType 只能为 standard 或 agreement")
    private String priceType;

    /** 计价币种，不传默认为人民币 */
    @Pattern(regexp = "^[A-Z]{3}$", message = "currency 必须为3位大写币种代码")
    private String currency;

    /** 单品单价 */
    @NotNull(message = "priceValue 不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    @Digits(integer = 16, fraction = 2, message = "单价最多16位整数、2位小数")
    private BigDecimal priceValue;

    /** 最小起订量MOQ */
    @NotNull(message = "moq 不能为空")
    @Min(value = 0, message = "moq 不能为负数")
    private Integer moq;

    /** 交付周期（天） */
    @NotNull(message = "leadTime 不能为空")
    @Min(value = 0, message = "leadTime 不能为负数")
    private Integer leadTime;

    /** 报价生效日期 */
    @NotNull(message = "effectDate 不能为空")
    private LocalDate effectDate;

    /** 报价失效日期，传 null 表示永久有效；非空时必须晚于生效日期 */
    private LocalDate expireDate;
}
