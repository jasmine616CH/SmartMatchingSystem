package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.SchemeConflictLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchemeConflictLogMapper extends BaseMapper<SchemeConflictLog> {
}
