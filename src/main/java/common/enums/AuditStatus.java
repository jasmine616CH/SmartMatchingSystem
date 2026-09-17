package common.enums;

import lombok.Getter;

/**
 * 审批任务状态枚举（audit_record.status）
 * 0-待审核 PENDING
 * 1-已通过 APPROVED
 * 2-已驳回 REJECTED
 * 3-已撤回 REVOKED
 * <p>
 * 描述的是「审批任务」而非业务对象，是独立于业务状态机的第三条生命周期
 * （有 REVOKED、无 PUBLISHED）。它与业务状态编码 0/1/2 的重合纯属巧合，
 * 不要与 {@link SupplierStatus} / {@link SupplierQualificationStatus} 混用。
 */
@Getter
public enum AuditStatus {

    PENDING(0, "PENDING", "待审核"),
    APPROVED(1, "APPROVED", "已通过"),
    REJECTED(2, "REJECTED", "已驳回"),
    REVOKED(3, "REVOKED", "已撤回");

    /** 数据库存储编码 */
    private final Integer code;
    /** 英文标识大写 */
    private final String enName;
    /** 中文描述 */
    private final String desc;

    AuditStatus(Integer code, String enName, String desc) {
        this.code = code;
        this.enName = enName;
        this.desc = desc;
    }

    /** 根据code查找枚举 */
    public static AuditStatus getByCode(Integer code) {
        for (AuditStatus e : AuditStatus.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 根据英文名称查找枚举 */
    public static AuditStatus getByEnName(String enName) {
        for (AuditStatus e : AuditStatus.values()) {
            if (e.getEnName().equals(enName)) {
                return e;
            }
        }
        return null;
    }

    /** 根据code取中文描述，未命中返回 null */
    public static String getNameByCode(Integer code) {
        AuditStatus e = getByCode(code);
        return e == null ? null : e.getDesc();
    }
}
