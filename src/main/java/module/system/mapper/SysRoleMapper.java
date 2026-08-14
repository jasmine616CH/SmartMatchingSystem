package module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应系统角色表 (sys_role)
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
