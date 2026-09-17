package module.compat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改冲突规则入参
 * <p>
 * 规则挂在二级子系统上（part_category.level = 2），其下所有三级配件类别自动适用。
 */
@Data
public class ConflictRuleUpdateDTO {

    /** 规则主键ID */
    @NotNull(message = "ruleId 不能为空")
    private Long ruleId;

    /** 规则名称 */
    @NotBlank(message = "规则名称不能为空")
    @Size(max = 100, message = "规则名称最多100字")
    private String ruleName;

    /** A侧二级子系统ID */
    @NotNull(message = "catAId 不能为空")
    private Long catAId;

    /** B侧二级子系统ID */
    @NotNull(message = "catBId 不能为空")
    private Long catBId;

    /** A侧参数编码 */
    @NotBlank(message = "paramCodeA 不能为空")
    @Size(max = 50, message = "A侧参数编码最多50字")
    private String paramCodeA;

    /** B侧参数编码 */
    @NotBlank(message = "paramCodeB 不能为空")
    @Size(max = 50, message = "B侧参数编码最多50字")
    private String paramCodeB;

    /**
     * Aviator 表达式，变量 a / b 分别为两侧配件的参数映射，返回 true 表示兼容
     * <p>如 a.VOLTAGE == b.VOLTAGE；参数编码含连字符等非法标识符时用下标写法 a["MOT-VOLTAGE"]
     */
    @NotBlank(message = "校验表达式不能为空")
    @Size(max = 1000, message = "校验表达式最多1000字")
    private String checkExpr;

    /** 约束强度：0-硬性冲突 1-偏好偏差；不传按 0 处理 */
    private Integer severity;

    /** 冲突描述 */
    @NotBlank(message = "冲突描述不能为空")
    @Size(max = 200, message = "冲突描述最多200字")
    private String errorMsg;

    /** 整改建议 */
    @Size(max = 500, message = "整改建议最多500字")
    private String solveSuggest;

    /** 状态：0-禁用 1-启用；不传按启用处理 */
    private Integer status;

    /** 执行顺序 */
    private Integer sort;
}
