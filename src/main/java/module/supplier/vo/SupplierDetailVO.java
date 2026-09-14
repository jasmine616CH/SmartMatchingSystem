package module.supplier.vo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 供应商详情
 * <p>
 * 联系人由原先扁平的 name/position/phone/email 改为 contacts 列表，
 * 因为一个供应商可以有多个联系人。
 */
@Data
public class SupplierDetailVO {

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

    /** 联系人列表 */
    private List<SupplierContactVO> contacts;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
