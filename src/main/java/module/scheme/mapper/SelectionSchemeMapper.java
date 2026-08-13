package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.SelectionScheme;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SelectionSchemeMapper extends BaseMapper<SelectionScheme> {
}
