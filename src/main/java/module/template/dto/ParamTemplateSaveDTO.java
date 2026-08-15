package module.template.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParamTemplateSaveDTO {

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

    // 状态：0-草稿 1-待审核 2-已发布
    @NotNull(message = "状态不能为空")
    private Integer status;

    // 审批人用户ID，草稿状态为空
    private Long auditUserId;

    // 模板备注说明
    @NotBlank(message = "模板备注说明不能为空")
    private String remark;
}
