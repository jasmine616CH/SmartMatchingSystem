package module.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 审批驳回入参
 */
@Data
public class AuditRejectDTO {

    /** 审批任务主键ID */
    @NotNull(message = "auditId 不能为空")
    private Long auditId;

    /** 驳回理由，必填：驳回必须让提交人知道改什么 */
    @NotBlank(message = "驳回理由不能为空")
    @Size(max = 500, message = "驳回理由最多500字")
    private String rejectReason;
}
