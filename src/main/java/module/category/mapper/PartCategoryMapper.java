package module.category.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import module.category.entity.PartCategory;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PartCategoryMapper extends BaseMapper<PartCategory> {

    long countByCatName(@Param("catName") String catName);

}
