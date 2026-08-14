package module.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统用户表 (user)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** 主键ID（雪花算法，业务生成） */
    private Long userId;

    /** 登录账号，全局唯一 */
    private String username;

    /** 加密后的登录密码 */
    private String password;

    /** 人员真实姓名 */
    private String realName;

    /** 联系手机号 */
    private String phone;

    /** 邮箱，选填 */
    private String email;

    /** 账号类型：super_admin/data_admin/supplier_admin/approver/design_engineer/purchase/solution_engineer */
    private String userType;

    /** 账号状态：0 冻结 1 正常 */
    private Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
