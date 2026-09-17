package module.review.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用审批记录表 (audit_record)
 * <p>
 * 一次「提交审核 → 审批决策」产生一行。因为被驳回或撤回的业务可以修改后重新提交，
 * 所以 (biz_type, biz_id) 上<b>故意不加唯一键</b>——多行累积正是审批历史的来源。
 * 「同一业务至多一条待审核」由 service 层配合业务表的状态 CAS 共同保证。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRecord {

    /** 主键ID（雪花算法，业务生成） */
    @TableId
    private Long auditId;

    /** 业务类型编码：SUPPLIER-供应商主体 SUPPLIER_QUALIFICATION-供应商资质 */
    private String bizType;

    /** 业务对象主键，含义由 bizType 决定 */
    private Long bizId;

    /** 审批状态：0-待审核 1-已通过 2-已驳回 3-已撤回 */
    private Integer status;

    /** 外键：sys_user.user_id 提交人用户ID */
    private Long submitUserId;

    /** 外键：sys_user.user_id 审批人用户ID，待审核状态为空 */
    private Long auditUserId;

    /** 审批完成时间，待审核状态为空 */
    private LocalDateTime auditTime;

    /** 驳回理由，仅驳回状态非空 */
    private String rejectReason;

    /** 提交时间（本行创建即提交审核时刻） */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
