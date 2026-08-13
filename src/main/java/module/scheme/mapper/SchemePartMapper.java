package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.SchemePart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchemePartMapper extends BaseMapper<SchemePart> {
}
