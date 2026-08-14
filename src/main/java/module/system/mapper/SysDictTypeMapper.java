package module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.system.entity.SysDictType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应字典类型表 (sys_dict_type)
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {
}
