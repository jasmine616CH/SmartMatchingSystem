package module.part.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PartInfoDTO {

    // 外键：part_category.cat_id 所属三级配件分类
    @NotNull(message = "catId 不能为空")
    private Long catId;

    // 全局唯一物料编码
    @NotBlank(message = "partCode 不能为空")
    private String partCode;

    // 配件品牌
    @NotBlank(message = "brand 不能为空")
    private String brand;

    // 配件型号
    @NotBlank(message = "model 不能为空")
    private String model;

    // 生命周期：new-新品 mass-量产 stop-停产
    @NotBlank(message = "lifeStatus 不能为空")
    private String lifeStatus;

    // 适配无人车平台/车型 
    @NotBlank(message = "adaptPlatform 不能为空")
    private String adaptPlatform;

    // 发布状态：0-草稿 1-正式发布，仅发布配件可参与选型
    @NotNull(message = "publishingStatus 不能为空")
    private Integer publishingStatus;

    // 配件维护负责人ID
    @NotNull(message = "maintainUserId 不能为空")
    private Long maintainUserId;

    // 配件备注说明
    @NotBlank(message = "remark 不能为空")
    private String remark;
}
