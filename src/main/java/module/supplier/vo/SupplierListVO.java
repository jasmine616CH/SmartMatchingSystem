package module.supplier.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 供应商列表行
 */
@Data
public class SupplierListVO {

    /** 供应商主键ID */
    private Long supplierId;

    /** 供应商企业全称 */
    private String supplierName;

    /** 统一社会信用代码 */
    private String creditCode;

    /** 企业注册/办公地址 */
    private String address;

    /** 可供应配件品类范围 */
    private String supplyScope;

    /** 状态：0-草稿 1-待审核 2-已发布 */
    private Integer status;

    /** 状态中文描述 */
    private String statusName;

    /** 审批人用户ID，草稿状态为空 */
    private Long auditUserId;

    /** 审批人姓名，草稿状态为空 */
    private String auditUserName;

    /** 供应商备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
