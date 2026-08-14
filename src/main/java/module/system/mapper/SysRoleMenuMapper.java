package module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应角色-菜单权限关联表 (sys_role_menu)
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
}
