package module.supplier.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 供应商资质列表查询入参
 * <p>
 * 全部筛选条件均为可选，不传即不过滤。
 */
@Data
public class SupplierQualificationQueryDTO {

    /** 所属供应商主键 */
    private Long supplierId;

    /** 资质证书名称，模糊匹配 */
    private String qfName;

    /** 证书编号，模糊匹配 */
    private String certNo;

    /**
     * 状态：0-草稿 1-待审核 2-有效 3-过期；不传即不过滤
     * <p>
     * 3-过期 是派生值，查询时会翻译成「状态为有效 且 已过失效日期」的日期条件；
     * 查 2-有效 时反之会附加「未过失效日期」，两者互斥。
     */
    private Integer status;

    /** 每页最大条数（不传默认10） */
    @Min(value = 1, message = "每页最小条数为1")
    @Max(value = 100, message = "每页最大数为100")
    private Integer pageSize = 10;

    /** 页码数（不传默认第1页） */
    @Min(value = 1, message = "页码最小数为1")
    private Integer pageNum = 1;
}
