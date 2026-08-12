package module.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户-角色关联表 (sys_user_role)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRole {

    /** 主键ID（雪花算法，业务生成） */
    private Long id;

    /** 外键：user.user_id 用户主键 */
    private Long userId;

    /** 外键：sys_role.role_id 角色主键 */
    private Long roleId;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
