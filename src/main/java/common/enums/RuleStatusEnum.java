package common.enums;

import lombok.Getter;

@Getter
public enum RuleStatusEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final Integer code;
    private final String desc;

    RuleStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

