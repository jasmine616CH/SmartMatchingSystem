package module.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增字典项入参
 */
@Data
public class DictItemSaveDTO {

    /** 所属字典类型ID */
    @NotNull(message = "dictTypeId 不能为空")
    private Long dictTypeId;

    /** 数据库存储真实值，同一字典内唯一 */
    @NotBlank(message = "字典项取值不能为空")
    @Size(max = 50, message = "字典项取值最多50字")
    private String value;

    /** 前端下拉展示文字 */
    @NotBlank(message = "字典项名称不能为空")
    @Size(max = 50, message = "字典项名称最多50字")
    private String label;

    /** 下拉选项排序，不传按 0 */
    private Integer sort;

    /** 状态：0-停用 1-启用；不传按启用处理 */
    private Integer status;
}
