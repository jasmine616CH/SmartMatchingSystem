package common.enums;

import java.util.List;

import lombok.Getter;

/**
 * 用户权限
 */
@Getter
public enum UserType {

    SUPER_ADMIN(0, "ROLE_SUPER_ADMIN", "超级管理员"),
    DATA_ADMIN(1, "ROLE_DATA_ADMIN", "数据管理员"),
    SUPPLIER_ADMIN(2, "ROLE_SUPPLIER_ADMIN", "供应商管理员"),
    APPROVER(3, "ROLE_APPROVER", "审批人"),
    DESIGN_ENGINEER(4, "ROLE_DESIGN_ENGINEER", "设计工程师"),
    PURCHASE(5, "ROLE_PURCHASE", "采购人"),
    SOLUTION_ENGINEER(6, "ROLE_SOLUTION_ENGINEER", "方案工程师");

    private final int code;
    private final String authority;
    private final String desc;

    /**
     * 注意参数顺序与字段声明一致：(code, authority, desc)
     * <p>枚举常量是按 (code, "ROLE_XXX", "中文") 传的，而原构造器把参数名写成了
     * (code, desc, authority)，导致 desc 与 authority 整个反了——
     * getDesc() 返回 "ROLE_XXX"、getAuthority() 返回中文。
     * SecurityUtils 里按 getAuthority() 比对 ROLE_APPROVER 的判断因此一直失效。
     */
    UserType(int code, String authority, String desc) {
        this.code = code;
        this.authority = authority;
        this.desc = desc;
    }

    /**
     * 根据枚举名称获取实例
     * 
     * @return 匹配的枚举对象
     */
    public static UserType getByCode(Integer code) {
        if (code == null)
            return null;
        for (UserType e : values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }

    /**
     * 超管可分配的角色
     * <p>
     * 不含超管本身（避免超管数量失控，需要第二个超管应走运维流程）、
     * 不含方案工程师（该角色由注册自助产生）、
     * 不含设计工程师（当前不启用）。
     */
    private static final List<UserType> SUPER_ADMIN_ASSIGNABLE =
            List.of(DATA_ADMIN, SUPPLIER_ADMIN, APPROVER, PURCHASE);

    /**
     * 自助注册产生的角色，固定为方案工程师
     */
    public static final UserType SELF_REGISTER_TYPE = SOLUTION_ENGINEER;

    /**
     * 超管可分配的角色列表
     */
    public static List<UserType> getSuperAdminAssignable() {
        return SUPER_ADMIN_ASSIGNABLE;
    }

    /**
     * 该角色是否允许由超管分配
     */
    public boolean isSuperAdminAssignable() {
        return SUPER_ADMIN_ASSIGNABLE.contains(this);
    }

    /**
     * 存库用的取值
     * <p>sys_user.user_type 存的是小写形式（super_admin / data_admin / ...），
     * 正好等于枚举名的小写。
     */
    public String getDbValue() {
        return this.name().toLowerCase();
    }

    /**
     * 宽松解析：接受 data_admin / DATA_ADMIN / dataAdmin 等写法
     * <p>比对时忽略大小写与下划线，解析不了返回 null 由调用方报错。
     *
     * @param input 前端传入的角色标识
     * @return 匹配的枚举，未命中返回 null
     */
    public static UserType fromInput(String input) {
        if (input == null) {
            return null;
        }
        String normalized = input.trim().toUpperCase().replace("_", "");
        if (normalized.isEmpty()) {
            return null;
        }
        for (UserType type : values()) {
            if (type.name().replace("_", "").equals(normalized)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据枚举名称获取代码
     *
     * @return 代码名称
     */
    public static String getNameByCode(Integer code) {
        if (code == null)
            return null;
        for (UserType e : values()) {
            if (e.getCode() == code) {
                return e.getDesc();
            }
        }
        return null;
    }
}
