package module.template.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PartCategorySaveDTO {

    // 分类名称
    @NotBlank(message = "分类名称不能为空")
    private String catName;

    // 分类全局唯一编码
    @NotBlank(message = "全局唯一编码不能为空")
    private String catCode;

    // 层级：1-系统 2-子系统 3-配件类别
    @NotNull(message = "层级不能为空")
    @Min(value = 1, message = "层级最小值为1")
    @Max(value = 3, message = "层级最大值为3")
    private Integer level;

    // 树形展示排序
    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序不能小于0")
    private Integer sort;

    // 分类业务说明
    @NotBlank(message = "分类业务说明不能为空")
    @Size(max = 255, message = "说明不能超过255个字符")
    private String remark;
}
