package module.part.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 配件档案详情
 * <p>
 * 比列表多带出分类名、模板名与维护人/审批人姓名，供详情页与编辑页使用。
 */
@Data
public class PartInfoDetailVO {

    /** 主键ID（雪花算法，业务生成） */
    private Long partId;

    /** 全局唯一物料编码 */
    private String partCode;

    /** 所属三级配件分类ID */
    private Long catId;

    /** 所属三级配件分类名称 */
    private String catName;

    /** 所属分类编码 */
    private String catCode;

    /** 录入配件时使用的参数模板ID */
    private Long templateId;

    /** 参数模板名称 */
    private String templateName;

    /** 配件品牌 */
    private String brand;

    /** 配件型号 */
    private String model;

    /** 生命周期：new-新品 mass-量产 stop-停产 */
    private String lifeStatus;

    /** 适配无人车平台/车型，多个用逗号分隔 */
    private String adaptPlatform;

    /** 发布状态：0-草稿 1-待审核 2-已发布 */
    private Integer publishingStatus;

    /** 发布状态中文描述 */
    private String publishingStatusName;

    /** 配件维护负责人ID */
    private Long maintainUserId;

    /** 配件维护负责人姓名 */
    private String maintainUserName;

    /** 审批人用户ID，草稿状态为空 */
    private Long auditUserId;

    /** 审批人姓名，草稿状态为空 */
    private String auditUserName;

    /** 配件备注说明 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
