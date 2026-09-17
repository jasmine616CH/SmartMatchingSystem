package common.enums;

import lombok.Getter;

/**
 * 校验规则的约束强度（param_field_check_rule.severity）
 * <p>
 * 用于区分「硬性约束」与「偏好条件」：
 * 硬性约束不满足时该候选件应被排除；偏好条件不满足只记为临界，并参与参数匹配度计算。
 */
@Getter
public enum RuleSeverity {

    /** 硬性约束：不满足即判为「不满足」 */
    HARD(0, "HARD", "硬性约束"),
    /** 偏好条件：不满足判为「临界」，参与匹配度计算 */
    PREFERENCE(1, "PREFERENCE", "偏好条件");

    /** 数据库存储编码 */
    private final Integer code;
    /** 英文标识大写 */
    private final String enName;
    /** 中文描述 */
    private final String desc;

    RuleSeverity(Integer code, String enName, String desc) {
        this.code = code;
        this.enName = enName;
        this.desc = desc;
    }

    /** 根据code查找枚举，未命中返回 null */
    public static RuleSeverity getByCode(Integer code) {
        for (RuleSeverity e : RuleSeverity.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 是否为硬性约束；severity 为空时按硬性处理，与库中默认值 0 保持一致 */
    public static boolean isHard(Integer severity) {
        return !PREFERENCE.getCode().equals(severity);
    }
}
