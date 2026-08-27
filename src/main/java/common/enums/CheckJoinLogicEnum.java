package common.enums;

import lombok.Getter;

@Getter
public enum CheckJoinLogicEnum {
    /** 并且，拼接表达式替换为 && */
    AND("AND", "并且", "&&"),
    /** 或者，拼接表达式替换为 || */
    OR("OR", "或者", "||");

    /** 数据库存储值 */
    private final String code;
    /** 前端展示文字 */
    private final String desc;
    /** Aviator实际运算符 */
    private final String aviatorOp;

    CheckJoinLogicEnum(String code, String desc, String aviatorOp) {
        this.code = code;
        this.desc = desc;
        this.aviatorOp = aviatorOp;
    }
}
