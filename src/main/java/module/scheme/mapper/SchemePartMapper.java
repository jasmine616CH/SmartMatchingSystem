package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.SchemePart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应方案候选配件明细表(scheme_part)
 */
@Mapper
public interface SchemePartMapper extends BaseMapper<SchemePart> {
}
