package module.template.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParamTemplateFieldListVO {
    // 主键ID（雪花算法，业务生成）
    @NotNull(message = "fieldId 不能为空")
    private Long fieldId;

    // 外键：param_template.template_id
    @NotNull(message = "templateId 不能为空")
    private Long templateId;

    // 参数唯一编码
    @NotBlank(message = "paramCode 不能为空")
    private String paramCode;

    // 参数中文名称
    @NotBlank(message = "paramCn 不能为空")
    private String paramCn;

    // 参数英文名称
    @NotBlank(message = "paramEn 不能为空")
    private String paramEn;

    // 必填类型：0-非必填 1-全局必填 2-条件必填
    @NotNull(message = "requiredType 不能为空")
    private Integer requiredType;

    // 页面展示排序
    @NotNull(message = "sort 不能为空")
    private Integer sort;
}
