package module.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色-菜单权限关联表 (sys_role_menu)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleMenu {

    /** 主键ID（雪花算法，业务生成） */
    private Long id;

    /** 外键：sys_role.role_id 角色主键 */
    private Long roleId;

    /** 外键：sys_menu.menu_id 菜单主键 */
    private Long menuId;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
