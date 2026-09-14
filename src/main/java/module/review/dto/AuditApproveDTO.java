package module.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批通过入参
 * <p>
 * 只认 auditId（审批任务身份），不接收 bizType / bizId——
 * 这正是审批接口能保持通用的关键。
 */
@Data
public class AuditApproveDTO {

    /** 审批任务主键ID */
    @NotNull(message = "auditId 不能为空")
    private Long auditId;
}
