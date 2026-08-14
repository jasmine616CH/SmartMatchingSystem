package module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应用户-角色关联表 (sys_user_role)
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
