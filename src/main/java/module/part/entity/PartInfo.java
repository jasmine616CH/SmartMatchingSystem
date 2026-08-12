package module.part.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配件档案主表 (part_info)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartInfo {

    /** 主键ID（雪花算法，业务生成） */
    private Long partId;

    /** 外键：part_category.cat_id 所属三级配件分类 */
    private Long catId;

    /** 全局唯一物料编码 */
    private String partCode;

    /** 配件品牌 */
    private String brand;

    /** 配件型号 */
    private String model;

    /** 生命周期：new-新品 mass-量产 stop-停产 */
    private String lifeStatus;

    /** 适配无人车平台/车型 */
    private String adaptPlatform;

    /** 发布状态：0-草稿 1-正式发布，仅发布配件可参与选型 */
    private Integer publishingStatus;

    /** 配件维护负责人ID */
    private Long maintainUserId;

    /** 配件备注说明 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
