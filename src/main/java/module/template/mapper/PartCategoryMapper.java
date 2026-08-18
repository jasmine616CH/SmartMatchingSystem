package module.template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import module.template.entity.PartCategory;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PartCategoryMapper extends BaseMapper<PartCategory> {

    long countByCatName(@Param("catName") String catName);

}
