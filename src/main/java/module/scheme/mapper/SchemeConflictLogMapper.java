package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.SchemeConflictLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应配件兼容冲突日志表(scheme_conflict_log)
 */
@Mapper
public interface SchemeConflictLogMapper extends BaseMapper<SchemeConflictLog> {
}
