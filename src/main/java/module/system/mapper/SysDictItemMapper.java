package module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.system.entity.SysDictItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应字典项明细表 (sys_dict_item)
 */
@Mapper
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {
}
