package module.supplier.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 供应商列表查询入参
 * <p>
 * 全部筛选条件均为可选，不传即不过滤。
 */
@Data
public class SupplierQueryDTO {

    /** 供应商企业全称，模糊匹配 */
    private String supplierName;

    /** 统一社会信用代码，模糊匹配 */
    private String creditCode;

    /** 联系人姓名，模糊匹配 */
    private String contactName;

    /** 状态：0-草稿 1-待审核 2-已发布；不传即不过滤 */
    private Integer status;

    /** 每页最大条数（不传默认10） */
    @Min(value = 1, message = "每页最小条数为1")
    @Max(value = 100, message = "每页最大数为100")
    private Integer pageSize = 10;

    /** 页码数（不传默认第1页） */
    @Min(value = 1, message = "页码最小数为1")
    private Integer pageNum = 1;
}
