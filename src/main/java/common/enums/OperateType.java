package common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 操作日志-业务类型枚举
 */
public enum OperateType implements IEnum<String> {

    ADD("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    /** 审核：提交审核、审批通过、审批驳回、撤回等状态流转 */
    AUDIT("审核"),
    QUERY("查询"),
    IMPORT("导入"),
    EXPORT("导出"),
    LOGIN("登录"),
    LOGOUT("登出");

    private final String desc;

    OperateType(String desc) {
        this.desc = desc;
    }

    @JsonValue
    public String getDesc() {
        return desc;
    }

    /**
     * MyBatis‑Plus存储到数据库的值，存中文描述，也可以存编码ADD/UPDATE
     */
    @Override
    public String getValue() {
        return this.desc;
    }
}
