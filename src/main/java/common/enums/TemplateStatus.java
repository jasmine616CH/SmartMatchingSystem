package common.enums;

import lombok.Getter;

/**
 * 模板状态枚举
 * 0-草稿 DRAFT
 * 1-待审核 PENDING_AUDIT
 * 2-已发布 PUBLISHED
 */
@Getter
public enum TemplateStatus {

    DRAFT(0, "DRAFT", "草稿"),
    PENDING_AUDIT(1, "PENDING_AUDIT", "待审核"),
    PUBLISHED(2, "PUBLISHED", "已发布");

    /** 数据库存储编码 */
    private final Integer code;
    /** 英文标识大写 */
    private final String enName;
    /** 中文描述 */
    private final String desc;

    TemplateStatus(Integer code, String enName, String desc) {
        this.code = code;
        this.enName = enName;
        this.desc = desc;
    }
    /** 根据code查找枚举 */
    public static TemplateStatus getByCode(Integer code) {
        for (TemplateStatus e : TemplateStatus.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 根据英文名称查找枚举 */
    public static TemplateStatus getByEnName(String enName) {
        for (TemplateStatus e : TemplateStatus.values()) {
            if (e.getEnName().equals(enName)) {
                return e;
            }
        }
        return null;
    }
}