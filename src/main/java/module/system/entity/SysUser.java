package module.system.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 系统用户表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser{

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

    /** 账号类型 */
    private String userType;

    /** 账号状态：0 冻结 1 正常 */
    public Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;

    
}