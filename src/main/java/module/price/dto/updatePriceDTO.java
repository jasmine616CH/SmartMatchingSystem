package module.price.dto;

import cn.hutool.core.date.DateTime;
import lombok.Data;

@Data
public class updatePriceDTO {

    /**
     * 配件
     */
    private String partName;

    /**
     * 配件id
     */
    private Long partId;

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
    private float  priceValue;

    /**
     * 最小起订量
     */
    private int moq;

    /**
     * 交付周期
     */
    private int leadTime;

    /**
     * 报价生效日期
     */
    private DateTime effectDate;

    /**
     * 报价失效日期
     */
    private DateTime expireDate;

    /**
     * 报价录入操作人员
     */
    private String createUserName;

    /**
     * 报价录入操作人员id
     */
    private int createUserId;

}
