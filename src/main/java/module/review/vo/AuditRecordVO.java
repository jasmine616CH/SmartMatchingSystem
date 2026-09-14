package module.review.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 审批任务列表行（待办列表 / 审批历史通用）
 */
@Data
public class AuditRecordVO {

    /** 审批任务主键ID */
    private Long auditId;

    /** 业务类型编码 */
    private String bizType;

    /** 业务类型中文描述 */
    private String bizTypeName;

    /** 业务对象主键 */
    private Long bizId;

    /** 审批状态：0-待审核 1-已通过 2-已驳回 3-已撤回 */
    private Integer status;

    /** 审批状态中文描述 */
    private String statusName;

    // ==================== 业务侧摘要（由各业务 Handler 提供） ====================

    /** 业务对象名称 */
    private String bizName;

    /** 业务对象业务编码 */
    private String bizCode;

    /** 业务对象当前状态编码 */
    private Integer bizStatus;

    /** 业务对象当前状态中文描述 */
    private String bizStatusName;

    /** 业务行是否已被删除；为 true 时该任务无法通过/驳回 */
    private Boolean bizDeleted;

    // ==================== 提交与审批信息 ====================

    /** 提交人用户ID */
    private Long submitUserId;

    /** 提交人姓名 */
    private String submitUserName;

    /** 提交时间 */
    private LocalDateTime createTime;

    /** 审批人用户ID，待审核状态为空 */
    private Long auditUserId;

    /** 审批人姓名，待审核状态为空 */
    private String auditUserName;

    /** 审批完成时间，待审核状态为空 */
    private LocalDateTime auditTime;

    /** 驳回理由，仅驳回状态非空 */
    private String rejectReason;
}
