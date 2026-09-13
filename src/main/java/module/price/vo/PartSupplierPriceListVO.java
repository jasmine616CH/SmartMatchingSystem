package module.price.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 配件供应商报价列表行
 * <p>
 * 覆盖前端表格所有可筛选/可排序的列：配件（编码/品牌/型号）、分类名、供应商名、
 * 报价本身（类型/币种/单价/MOQ/交付周期/生效失效日期）与录入信息。
 */
@Data
public class PartSupplierPriceListVO {

    /** 报价主键ID */
    private Long priceId;

    /** 配件供应商关联主键 */
    private Long psId;

    /** 配件主键 */
    private Long partId;

    /** 全局唯一物料编码 */
    private String partCode;

    /** 配件品牌 */
    private String brand;

    /** 配件型号 */
    private String model;

    /** 生命周期：new-新品 mass-量产 stop-停产 */
    private String lifeStatus;

    /** 配件分类ID */
    private Long catId;

    /** 配件分类名称 */
    private String catName;

    /** 供应商主键 */
    private Long supplierId;

    /** 供应商企业全称 */
    private String supplierName;

    /** 供应商状态：0-停用 1-启用 */
    private Integer supplierStatus;

    /** 供货类型：main-主供 spare-备供 wait-待认证 */
    private String supplyType;

    /** 配件-供应商认证状态：0-未认证 1-认证通过 */
    private Integer authStatus;

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

    /** 报价生效日期 */
    private LocalDate effectDate;

    /** 报价失效日期，为空永久有效 */
    private LocalDate expireDate;

    /** 报价录入操作人员ID */
    private Long createUserId;

    /** 报价录入操作人员姓名 */
    private String createUserName;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
