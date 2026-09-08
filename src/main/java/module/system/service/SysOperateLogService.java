package module.system.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import module.system.entity.SysOperateLog;
import module.system.mapper.SysOperateLogMapper;

@Service 
public class SysOperateLogService extends ServiceImpl<SysOperateLogMapper, SysOperateLog> {
    
    @Async
    public void saveOperateLog(SysOperateLog log) {
        baseMapper.insert(log);
    }
}
