package common.enums;

import lombok.Getter;

/**
 * 供应商主体状态枚举
 * 0-草稿 DRAFT
 * 1-待审核 PENDING_AUDIT
 * 2-已发布 PUBLISHED
 * <p>
 * 与 {@link PublishStatus} 状态值同构（0草稿/1待审核/2已发布），便于审批流共用同一套状态守卫写法。
 */
@Getter
public enum SupplierStatus {

    DRAFT(0, "DRAFT", "草稿"),
    PENDING_AUDIT(1, "PENDING_AUDIT", "待审核"),
    PUBLISHED(2, "PUBLISHED", "已发布");

    /** 数据库存储编码 */
    private final Integer code;
    /** 英文标识大写 */
    private final String enName;
    /** 中文描述 */
    private final String desc;

    SupplierStatus(Integer code, String enName, String desc) {
        this.code = code;
        this.enName = enName;
        this.desc = desc;
    }

    /** 根据code查找枚举 */
    public static SupplierStatus getByCode(Integer code) {
        for (SupplierStatus e : SupplierStatus.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 根据英文名称查找枚举 */
    public static SupplierStatus getByEnName(String enName) {
        for (SupplierStatus e : SupplierStatus.values()) {
            if (e.getEnName().equals(enName)) {
                return e;
            }
        }
        return null;
    }

    /** 根据code取中文描述，未命中返回 null */
    public static String getNameByCode(Integer code) {
        SupplierStatus e = getByCode(code);
        return e == null ? null : e.getDesc();
    }
}
