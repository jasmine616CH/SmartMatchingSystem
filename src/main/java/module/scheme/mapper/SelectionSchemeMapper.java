package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.SelectionScheme;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应选型方案主表(selection_scheme)
 */
@Mapper
public interface SelectionSchemeMapper extends BaseMapper<SelectionScheme> {
}
