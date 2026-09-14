package common.enums;

import java.time.LocalDate;

import lombok.Getter;

/**
 * 供应商资质状态枚举
 * 0-草稿 DRAFT
 * 1-待审核 PENDING_AUDIT
 * 2-有效 VALID
 * 3-过期 EXPIRED
 * <p>
 * 注意：{@link #EXPIRED} 是<b>读时派生</b>值，禁止写入数据库。
 * 失效日期是否已过是「已存数据 + 当前时钟」的纯函数，落库就需要定时任务，
 * 而两次任务之间存的值必然是错的（证书过期一小时后仍会显示「有效」）。
 * 因此审批通过永远写 {@link #VALID}，展示层再按 expire_date 投影为「过期」，
 * 这样把 expire_date 延长后无需任何反向状态迁移即可自动恢复「有效」。
 */
@Getter
public enum SupplierQualificationStatus {

    DRAFT(0, "DRAFT", "草稿"),
    PENDING_AUDIT(1, "PENDING_AUDIT", "待审核"),
    VALID(2, "VALID", "有效"),
    /** 派生值，禁止入库，仅用于展示与查询条件翻译 */
    EXPIRED(3, "EXPIRED", "过期");

    /** 数据库存储编码 */
    private final Integer code;
    /** 英文标识大写 */
    private final String enName;
    /** 中文描述 */
    private final String desc;

    SupplierQualificationStatus(Integer code, String enName, String desc) {
        this.code = code;
        this.enName = enName;
        this.desc = desc;
    }

    /** 根据code查找枚举 */
    public static SupplierQualificationStatus getByCode(Integer code) {
        for (SupplierQualificationStatus e : SupplierQualificationStatus.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /** 根据英文名称查找枚举 */
    public static SupplierQualificationStatus getByEnName(String enName) {
        for (SupplierQualificationStatus e : SupplierQualificationStatus.values()) {
            if (e.getEnName().equals(enName)) {
                return e;
            }
        }
        return null;
    }

    /** 根据code取中文描述，未命中返回 null */
    public static String getNameByCode(Integer code) {
        SupplierQualificationStatus e = getByCode(code);
        return e == null ? null : e.getDesc();
    }

    /**
     * 派生展示状态：仅「有效」且已过失效日期时投影为「过期」，其余原样返回。
     * <p>
     * 这是「派生状态」的唯一收口处，查询过滤需要按过期筛时也应复用这里的判定口径。
     *
     * @param storedStatus 数据库实际存储的状态
     * @param expireDate   证书失效日期
     * @return 展示用状态编码
     */
    public static Integer resolveDisplayStatus(Integer storedStatus, LocalDate expireDate) {
        if (VALID.getCode().equals(storedStatus) && expireDate != null
                && expireDate.isBefore(LocalDate.now())) {
            return EXPIRED.getCode();
        }
        return storedStatus;
    }

    /**
     * 派生展示状态的中文描述
     *
     * @param storedStatus 数据库实际存储的状态
     * @param expireDate   证书失效日期
     * @return 展示用状态中文名
     */
    public static String resolveDisplayStatusName(Integer storedStatus, LocalDate expireDate) {
        return getNameByCode(resolveDisplayStatus(storedStatus, expireDate));
    }
}
