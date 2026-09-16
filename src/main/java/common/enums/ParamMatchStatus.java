package common.enums;

import lombok.Getter;

/**
 * 参数比对判定态
 * <p>
 * 用于候选件并排比较：每个配件在每个参数上落到四态之一。
 * 与方案参数校验（SchemeValidateResultVO）用的是同一套判定口径，
 * 只是那里按配件聚合，这里按「参数 × 配件」展开成矩阵。
 */
@Getter
public enum ParamMatchStatus {

    /** 有值且全部校验规则通过 */
    SATISFIED(0, "SATISFIED", "满足"),
    /** 有值但偏好条件未通过（severity=1） */
    WARNING(1, "WARNING", "临界"),
    /** 有值但硬性约束未通过（severity=0） */
    VIOLATED(2, "VIOLATED", "不满足"),
    /** 没有录入该参数的值 */
    MISSING(3, "MISSING", "缺失");

    /** 编码 */
    private final Integer code;
    /** 英文标识大写 */
    private final String enName;
    /** 中文描述 */
    private final String desc;

    ParamMatchStatus(Integer code, String enName, String desc) {
        this.code = code;
        this.enName = enName;
        this.desc = desc;
    }

    /** 根据code查找枚举，未命中返回 null */
    public static ParamMatchStatus getByCode(Integer code) {
        for (ParamMatchStatus e : ParamMatchStatus.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
