package module.scheme.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceVO {

    /** 单品价格 */
    private BigDecimal priceValue;

    /** 计价币种 */
    private String currency;

}
