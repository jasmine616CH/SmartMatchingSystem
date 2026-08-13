package module.audit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.audit.entity.SysAuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysAuditLogMapper extends BaseMapper<SysAuditLog> {
}
