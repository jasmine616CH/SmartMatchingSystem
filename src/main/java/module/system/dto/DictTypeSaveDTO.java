package module.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增字典类型入参
 */
@Data
public class DictTypeSaveDTO {

    /**
     * 字典大类编码
     * <p>SYS-系统通用类, PARAM-参数模板配置类, ACCESSORY-配件业务字典类, RULE-规则与选型类
     */
    @NotBlank(message = "字典大类不能为空")
    @Size(max = 30, message = "字典大类最多30字")
    private String dictCategory;

    /** 字典唯一编码，全局唯一 */
    @NotBlank(message = "字典编码不能为空")
    @Size(max = 50, message = "字典编码最多50字")
    private String dictCode;

    /** 字典分类中文名称 */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 50, message = "字典名称最多50字")
    private String dictName;

    /** 状态：0-停用 1-启用；不传按启用处理 */
    private Integer status;
}
