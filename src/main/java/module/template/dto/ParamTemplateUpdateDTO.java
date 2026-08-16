package module.template.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParamTemplateUpdateDTO {

    /** 主键ID（雪花算法，业务生成） */
    @NotNull(message = "templateId 不能为空")
    private Long templateId;

    // 外键：part_category.cat_id
    @NotNull(message = "分类ID不能为空")
    private Long catId;

    // 模板名称
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    // 模板版本号
    @NotBlank(message = "模板版本号不能为空")
    private String version;

    // 模板生效日期
    @NotNull(message = "模板生效日期不能为空")
    private LocalDateTime effectDate;

    // 失效日期，为空代表永久有效
    private LocalDateTime expireDate;

    // 模板备注说明
    @NotBlank(message = "模板备注说明不能为空")
    private String remark;
}
