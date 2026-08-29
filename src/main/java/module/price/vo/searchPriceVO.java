package module.price.vo;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class searchPriceVO {

    /**
     * 配件
     */
    private String catName;

    /**
     * 供应商
     */
    private String supplierName;

    /**
     * 配件品牌
     */
    private String brand;

    /**
     * 配件型号
     */
    private String model;

    /**
     * 价格种类
     */
    private String priceType;

    /**
     * 计价币种
     */
    private String currency;

    /**
     * 单价
     */
    private BigDecimal priceValue;

    /**
     * 最小起订量
     */
    private Integer moq;

    /**
     * 交付周期
     */
    private Integer leadTime;

    /**
     * 报价生效日期
     */
    private LocalDateTime effectDate;

}
