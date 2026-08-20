package module.system.vo;

import cn.hutool.core.date.DateTime;

/**
 * 管理员管理用户账号返回Vo
 */
public class QueryInformationVo {

    /**
     * 真实姓名
     */
    private String real_name;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private String userType;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 账号状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private DateTime createTime;

}
