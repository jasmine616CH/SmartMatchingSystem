package module.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 审批任务列表查询入参
 */
@Data
public class AuditTaskQueryDTO {

    /**
     * 业务类型编码：SUPPLIER / SUPPLIER_QUALIFICATION
     * <p>
     * 声明为 String 而非 BizType 枚举：查询参数的枚举绑定走 Spring 的
     * StringToEnumConverterFactory → Enum.valueOf(name())，@JsonValue 对 query param
     * 不生效，直接用枚举会让 ?bizType=供应商主体 静默 400。用 String 再显式解析，
     * 报错信息也更清楚。不传即不过滤。
     */
    private String bizType;

    /**
     * 审批状态：0-待审核 1-已通过 2-已驳回 3-已撤回
     * <p>
     * 故意<b>不设默认值</b>：若默认为 0，前端就无法把该条件从查询串里清空。
     * 待办页面固定传 status=0。
     */
    private Integer status;

    /** 业务对象主键 */
    private Long bizId;

    /** 提交人用户ID */
    private Long submitUserId;

    /** 审批人用户ID */
    private Long auditUserId;

    /** 每页最大条数（不传默认10） */
    @Min(value = 1, message = "每页最小条数为1")
    @Max(value = 100, message = "每页最大数为100")
    private Integer pageSize = 10;

    /** 页码数（不传默认第1页） */
    @Min(value = 1, message = "页码最小数为1")
    private Integer pageNum = 1;
}
