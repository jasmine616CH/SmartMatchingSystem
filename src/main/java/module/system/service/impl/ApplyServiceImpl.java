package module.system.service.impl;

import lombok.SneakyThrows;
import module.system.dto.signInDTO;
import module.system.mapper.UserMapper;
import module.system.service.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统申请账号业务接口实现类
 * 实现方案工程师的申请账号
 */
@Service
public class ApplyServiceImpl implements ApplyService {


    @Autowired
    private UserMapper UserMapper;

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(signInDTO request){
        UserMapper.add(request);
    }
}
