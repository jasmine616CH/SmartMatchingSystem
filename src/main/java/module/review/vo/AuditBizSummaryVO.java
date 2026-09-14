package module.review.vo;

import lombok.Data;

/**
 * 待办 / 历史列表中一行的业务侧摘要
 * <p>
 * 由各业务模块的 BizAuditHandler 提供。审批中心不认识具体业务，只展示这三个字段。
 */
@Data
public class AuditBizSummaryVO {

    /** 业务对象名称（供应商企业全称 / 资质证书名称） */
    private String bizName;

    /** 业务对象业务编码（统一社会信用代码 / 证书编号） */
    private String bizCode;

    /** 业务对象当前状态编码（已按业务规则派生，如资质过期投影为 3） */
    private Integer bizStatus;

    /** 业务对象当前状态中文描述 */
    private String bizStatusName;
}
