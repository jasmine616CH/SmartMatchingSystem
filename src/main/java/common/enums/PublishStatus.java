package common.enums;

import lombok.Getter;

/**
 * 配件主数据的发布状态
 * <p>
 * 「草稿 → 待审核 → 已发布」这一套生命周期被多个主数据对象共用：
 * 配件参数模板(param_template.status)、配件档案(part_info.publishing_status)、
 * 配件分类(part_category.status)。它们的状态语义完全一致，故共用一个枚举，
 * 避免各写一份导致漂移。
 * <p>
 * 「审核」本身由通用审批中心统一管理（audit_record + BizAuditHandler），
 * 本枚举只描述业务对象所处的阶段。
 */
@Getter
public enum PublishStatus {

    DRAFT(0, "DRAFT", "草稿"),
    PENDING_AUDIT(1, "PENDING_AUDIT", "待审核"),
    PUBLISHED(2, "PUBLISHED", "已发布");

    /** 数据库存储编码 */
    private final Integer code;
    /** 英文标识大写 */
    private final String enName;
    /** 中文描述 */
    private final String desc;

    PublishStatus(Integer code, String enName, String desc) {
        this.code = code;
        this.enName = enName;
        this.desc = desc;
    }

    /** 根据code查找枚举，未命中返回 null */
    public static PublishStatus getByCode(Integer code) {
        for (PublishStatus e : PublishStatus.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 根据英文名称查找枚举，未命中返回 null */
    public static PublishStatus getByEnName(String enName) {
        for (PublishStatus e : PublishStatus.values()) {
            if (e.getEnName().equals(enName)) {
                return e;
            }
        }
        return null;
    }

    /** 根据code取中文描述，未命中返回 null */
    public static String getNameByCode(Integer code) {
        PublishStatus e = getByCode(code);
        return e == null ? null : e.getDesc();
    }
}
