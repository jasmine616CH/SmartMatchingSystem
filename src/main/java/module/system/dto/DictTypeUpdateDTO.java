package module.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改字典类型入参
 */
@Data
public class DictTypeUpdateDTO {

    /** 字典类型主键ID */
    @NotNull(message = "dictTypeId 不能为空")
    private Long dictTypeId;

    /** 字典大类编码 */
    @NotBlank(message = "字典大类不能为空")
    @Size(max = 30, message = "字典大类最多30字")
    private String dictCategory;

    /**
     * 字典唯一编码
     * <p>参数模板已按 dictCode 关联字典，改编码会让既有引用失效，服务端会拒绝修改。
     */
    @NotBlank(message = "字典编码不能为空")
    @Size(max = 50, message = "字典编码最多50字")
    private String dictCode;

    /** 字典分类中文名称 */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 50, message = "字典名称最多50字")
    private String dictName;

    /** 状态：0-停用 1-启用 */
    private Integer status;
}
