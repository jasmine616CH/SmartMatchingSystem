package module.part.vo;

import lombok.Data;

@Data
public class PartInfoListVO {

    /** 主键ID（雪花算法，业务生成） */
    private Long partId;

    /** 全局唯一物料编码 */
    private String partCode;

    /** 外键：part_category.cat_id 所属三级配件分类 */
    private Long catId;

    /** 所属三级配件分类名称 */
    private String catName;

    /** 外键：part_template.template_id 所属配件模板 */
    private Long templateId;

    /** 配件品牌 */
    private String brand;

    /** 配件型号 */
    private String model;

    /** 生命周期：new-新品 mass-量产 stop-停产 */
    private String lifeStatus;

    /** 适配无人车平台/车型，多个用逗号分隔 */
    private String adaptPlatform;

    /** 发布状态：0-草稿 1-待审核 2-已发布，仅已发布配件可参与选型 */
    private Integer publishingStatus;

    /** 发布状态中文描述 */
    private String publishingStatusName;

    /** 配件维护负责人ID */
    private Long maintainUserId;

    /** 配件维护负责人名称 */
    private String maintainUserName;

    /** 配件备注说明 */
    private String remark;

    /** 外键：sys_user.user_id 审批人用户ID，草稿状态为空 */
    private Long auditUserId;
}
