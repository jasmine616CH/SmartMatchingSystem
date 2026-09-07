package module.part.vo;

import lombok.Data;

@Data
public class PartInfoListVO {

    /** 主键ID（雪花算法，业务生成） */
    private Long partId;

    /** 外键：part_category.cat_id 所属三级配件分类 */
    private Long catId;

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

    /** 配件维护负责人名称 */
    private String maintainUserName;

    /** 配件备注说明 */
    private String remark;
}
