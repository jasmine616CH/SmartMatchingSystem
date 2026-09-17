package module.compat.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 冲突规则列表查询入参
 */
@Data
public class ConflictRuleQueryDTO {

    /** 规则名称，模糊匹配；不传即不过滤 */
    private String ruleName;

    /** A侧二级子系统ID */
    private Long catAId;

    /** B侧二级子系统ID */
    private Long catBId;

    /** 状态：0-禁用 1-启用；不传即不过滤 */
    private Integer status;

    /** 每页最大条数（不传默认10） */
    @Min(value = 1, message = "每页最小条数为1")
    @Max(value = 100, message = "每页最大数为100")
    private Integer pageSize = 10;

    /** 页码数（不传默认第1页） */
    @Min(value = 1, message = "页码最小数为1")
    private Integer pageNum = 1;
}
