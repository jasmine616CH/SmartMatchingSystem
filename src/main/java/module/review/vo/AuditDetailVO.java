package module.review.vo;

import lombok.Data;

/**
 * 审批详情：审批任务信息 + 业务明细
 * <p>
 * bizDetail 内联展开各业务模块自己的 DetailVO，类型为 Object，
 * 使审批中心无需依赖任何业务 VO 类型。
 */
@Data
public class AuditDetailVO {

    /** 审批任务信息（含业务摘要） */
    private AuditRecordVO audit;

    /** 业务明细，由对应 BizAuditHandler.loadDetail 提供；业务行已删除时为 null */
    private Object bizDetail;
}
