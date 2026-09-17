package module.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 字典类型列表查询入参
 */
@Data
public class DictTypeQueryDTO {

    /** 字典大类：SYS / PARAM / ACCESSORY / RULE；不传即不过滤 */
    private String dictCategory;

    /** 字典编码，模糊匹配 */
    private String dictCode;

    /** 字典名称，模糊匹配 */
    private String dictName;

    /** 状态：0-停用 1-启用；不传即不过滤 */
    private Integer status;

    /** 每页最大条数（不传默认10） */
    @Min(value = 1, message = "每页最小条数为1")
    @Max(value = 100, message = "每页最大数为100")
    private Integer pageSize = 10;

    /** 页码数（不传默认第1页） */
    @Min(value = 1, message = "页码最小数为1")
    private Integer pageNum = 1;
}
