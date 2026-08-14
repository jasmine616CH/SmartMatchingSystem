package module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应菜单路由表 (sys_menu)
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
}
