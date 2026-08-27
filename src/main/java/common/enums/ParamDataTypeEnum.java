package common.enums;

import lombok.Getter;
import java.util.Arrays;
import java.util.Optional;

/**
 * 参数数据类型枚举
 */
@Getter
public enum ParamDataTypeEnum {
    /**
     * 数值
     */
    NUMBER(1, "数值", "number"),
    /**
     * 枚举
     */
    ENUM(2, "枚举", "enum"),
    /**
     * 布尔
     */
    BOOLEAN(3, "布尔", "boolean"),
    /**
     * 文本
     */
    TEXT(4, "文本", "text"),
    /**
     * 日期
     */
    DATE(5, "日期", "date");

    /**
     * 序号编码 1,2,3,4,5
     */
    private final Integer code;
    /**
     * 前端展示文字
     */
    private final String label;
    /**
     * 英文标识
     */
    private final String type;

    ParamDataTypeEnum(Integer code, String label, String type) {
        this.code = code;
        this.label = label;
        this.type = type;
    }

    /**
     * 根据数字code获取枚举
     */
    public static Optional<ParamDataTypeEnum> getByCode(Integer code) {
        return Arrays.stream(ParamDataTypeEnum.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst();
    }

    /**
     * 根据label文字获取枚举
     */
    public static Optional<ParamDataTypeEnum> getByLabel(String label) {
        return Arrays.stream(ParamDataTypeEnum.values())
                .filter(e -> e.getLabel().equals(label))
                .findFirst();
    }

    /**
     * 根据type英文标识获取枚举
     */
    public static Optional<ParamDataTypeEnum> getByType(String type) {
        return Arrays.stream(ParamDataTypeEnum.values())
                .filter(e -> e.getType().equals(type))
                .findFirst();
    }
}
