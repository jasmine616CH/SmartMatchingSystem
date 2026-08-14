package common.enums;

import lombok.Getter;

/**
 * 用户权限
 */
@Getter
public enum userType {

    SUPER_ADMIN(0,"ROLE_SUPER_ADMIN", "超级管理员"),
    DATA_ADMIN(1,"ROLE_DATA_ADMIN","数据管理员"),
    SUPPLIER_ADMIN(2,"ROLE_SUPPLIER_ADMIN" , "供应商管理员"),
    APPROVER(3, "ROLE_APPROVER","审批人"),
    DESIGN_ENGINEER(4, "ROLE_DESIGN_ENGINEER", "设计工程师"),
    PURCHASE(5, "PURCHASE","采购人"),
    SOLUTION_ENGINEER(6, "ROLE_SOLUTION_ENGINEER","方案工程师")
    ;

    private final int code;
    private final String authority;
    private final String desc;

    userType(int code , String desc , String authority){
        this.code = code ;
        this.authority = authority;
        this.desc = desc;
    }

    /**
     *根据枚举名称获取实例
     * @return 匹配的枚举对象
     */
    public static userType getByCode(Integer code){
        if (code == null)
            return null;
        for (userType e : values()){
            if (e.getCode() == code){
                return e;
            }
        }
        return null;
    }

    /**
     * 根据枚举名称获取代码
     * @return 代码名称
     */
    public static String getNameByCode(Integer code){
        if (code == null )
            return null;
        for (userType e : values()){
            if (e.getCode() == code){
                return e.getDesc();
            }
        }
        return null;
    }
}
