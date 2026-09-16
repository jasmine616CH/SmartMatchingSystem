package module.scheme.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 方案配件明细分页查询入参
 */
@Data
public class SchemePartQueryDTO {

    /** 物料编码 / 品牌 / 型号，模糊匹配；不传即不过滤 */
    private String keyword;

    /** 三级分类名称，模糊匹配 */
    private String catName;

    /** 配件品牌，模糊匹配 */
    private String brand;

    /**
     * 排序字段
     * <p>只允许 quantity / unitPrice / leadTime / matchScore 四个白名单值，
     * 服务端映射成真实列名后再拼进 SQL，避免排序字段注入。
     */
    @Pattern(regexp = "^(quantity|unitPrice|leadTime|matchScore)$", message = "sortField 只支持 quantity、unitPrice、leadTime、matchScore")
    private String sortField;

    /** 排序方向：asc / desc，不传默认 desc */
    @Pattern(regexp = "^(asc|desc)$", message = "sortOrder 只支持 asc 或 desc")
    private String sortOrder;

    /** 每页最大条数（不传默认10） */
    @Min(value = 1, message = "每页最小条数为1")
    @Max(value = 100, message = "每页最大数为100")
    private Integer pageSize = 10;

    /** 页码数（不传默认第1页） */
    @Min(value = 1, message = "页码最小数为1")
    private Integer pageNum = 1;
}
