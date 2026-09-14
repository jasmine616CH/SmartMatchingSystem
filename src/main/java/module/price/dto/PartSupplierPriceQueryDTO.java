package module.price.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 配件供应商报价列表查询入参
 * <p>
 * 全部筛选条件均为可选，不传即不过滤。字段与前端表格列一一对应：
 * 前端用表格组件做列筛选时按当前页数据过滤，若需要全量筛选，
 * 把对应列的值作为查询参数传给 /api/price/list 即可，后端无需改动。
 */
@Data
public class PartSupplierPriceQueryDTO {

    // ==================== 精确匹配 ====================

    /** 配件供应商关联主键 */
    private Long psId;

    /** 配件主键 */
    private Long partId;

    /** 供应商主键 */
    private Long supplierId;

    /** 配件分类ID */
    private Long catId;

    /** 供货类型：main-主供 spare-备供 wait-待认证 */
    private String supplyType;

    /** 价格类型：standard-标准价 agreement-协议框架价 */
    private String priceType;

    /** 计价币种 */
    private String currency;

    /** 配件-供应商认证状态：0-未认证 1-认证通过；不传即不过滤 */
    private Integer authStatus;

    /** 配件发布状态：0-草稿 1-正式发布；不传即不过滤 */
    private Integer publishingStatus;

    /** 供应商状态：0-草稿 1-待审核 2-已发布；不传即不过滤 */
    private Integer supplierStatus;

    // ==================== 模糊匹配 ====================

    /** 物料编码，模糊匹配 */
    private String partCode;

    /** 配件品牌，模糊匹配 */
    private String brand;

    /** 配件型号，模糊匹配 */
    private String model;

    /** 供应商名称，模糊匹配 */
    private String supplierName;

    /** 配件分类名称，模糊匹配 */
    private String catName;

    // ==================== 其他 ====================

    /**
     * 是否只看当前生效的报价
     * <p>true 时要求 effect_date &lt;= 今天 且（expire_date 为空 或 expire_date &gt;= 今天）；
     * 不传或 false 则不加日期过滤，过期与未生效的报价同样返回。
     */
    private Boolean onlyValid;

    /** 每页最大条数（不传默认10） */
    @Min(value = 1, message = "每页最小条数为1")
    @Max(value = 100, message = "每页最大数为100")
    private Integer pageSize = 10;

    /** 页码数（不传默认第1页） */
    @Min(value = 1, message = "页码最小数为1")
    private Integer pageNum = 1;
}
