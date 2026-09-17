package module.compat.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配件兼容冲突规则表 (conflict_rule)
 * <p>
 * 定义「两个配件之间什么算冲突」。此前只有 scheme_conflict_log 记录检测结果，
 * 没有规则定义表，所以跨配件兼容性校验无从实现。
 * <p>
 * 规则挂在<b>二级子系统</b>上（part_category.level = 2），其下所有三级配件类别自动适用，
 * 一条规则管一片，避免按三级类别逐对穷举。
 * <p>
 * 规则<b>有方向</b>：cat_a_id / param_code_a 是一侧，cat_b_id / param_code_b 是另一侧，
 * 检测时按有序对枚举。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("conflict_rule")
public class ConflictRule {

    /** 主键ID（雪花算法，业务生成） */
    @TableId
    private Long ruleId;

    /** 规则名称 */
    private String ruleName;

    /** 外键：part_category.cat_id A侧二级子系统 */
    private Long catAId;

    /** 外键：part_category.cat_id B侧二级子系统 */
    private Long catBId;

    /** A侧需要读取的参数编码，两侧都取到值才能判定 */
    private String paramCodeA;

    /** B侧需要读取的参数编码，两侧都取到值才能判定 */
    private String paramCodeB;

    /** Aviator表达式，变量 a/b 为两侧配件的参数映射，true表示兼容 false表示冲突 */
    private String checkExpr;

    /** 约束强度：0-硬性冲突 1-偏好偏差 */
    private Integer severity;

    /** 冲突描述 */
    private String errorMsg;

    /** 整改建议 */
    private String solveSuggest;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    /** 执行顺序 */
    private Integer sort;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
