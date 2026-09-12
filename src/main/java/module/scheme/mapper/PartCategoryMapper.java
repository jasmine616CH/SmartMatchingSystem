package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.PartCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应配件三级分类表（树形结构）(part_category)
 */
@Mapper
public interface PartCategoryMapper extends BaseMapper<PartCategory> {
}
