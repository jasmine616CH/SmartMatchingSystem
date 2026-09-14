package common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 可审批业务类型枚举（audit_record.biz_type）
 * <p>
 * 它是持久化列的取值字典，供应商模块与审批中心都要引用，故放在 common 而非 review 模块。
 * <p>
 * 扩展方式：接入一种新的可审批业务 = 在此加一个常量 + 在对应业务模块实现一个
 * {@code module.review.spi.BizAuditHandler}，无需改动任何表结构与审批中心代码。
 * 例如后续接入配件主数据（配件分类 / 参数模板 / 配件档案 / 生命周期 / 适配平台）时，
 * 在此追加常量即可。
 */
public enum BizType implements IEnum<String> {

    SUPPLIER("SUPPLIER", "供应商主体"),
    SUPPLIER_QUALIFICATION("SUPPLIER_QUALIFICATION", "供应商资质");

    /** 数据库存储编码 */
    private final String code;
    /** 展示中文名称 */
    private final String desc;

    BizType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return this.code;
    }

    /**
     * 根据数据库存储编码查找枚举
     *
     * @param code 业务类型编码
     * @return 匹配的枚举，未命中返回 null
     */
    public static BizType getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (BizType e : BizType.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据枚举名称查找枚举
     *
     * @param name 枚举名称，如 SUPPLIER
     * @return 匹配的枚举，未命中返回 null
     */
    public static BizType getByEnName(String name) {
        if (name == null) {
            return null;
        }
        for (BizType e : BizType.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
