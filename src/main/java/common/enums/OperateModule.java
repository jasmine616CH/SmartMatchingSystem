package common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 操作模块枚举
 */
public enum OperateModule implements IEnum<String> {

    SUPPLIER("SUPPLIER", "供应商模块"),
    PRICE("PRICE", "价格模块"),
    SCHEME("SELECTION_SCHEME", "选型方案模块"),
    PART("ACCESSORY_ARCHIVE", "配件档案模块"),
    TEMPLATE("ACCESSORY_PARAM", "配件参数模块"),
    DICT("DICT", "字典模块"),
    COMPATIBLE_RULE("COMPATIBLE_RULE", "兼容规则模块"),
    AUDIT("APPROVE", "审批模块"),
    USER_PERMISSION("USER_PERMISSION", "用户与权限模块");

    /** 数据库存储编码 */
    private final String code;
    /** 展示中文名称 */
    private final String desc;

    OperateModule(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    public String getDesc() {
        return desc;
    }
    
    @Override
    public String getValue() {
        return this.code;
    }
}
