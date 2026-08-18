package module.system.service.impl;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.RequiredArgsConstructor;
import module.system.entity.SysUser;
import module.system.mapper.SysUserMapper;
import module.system.service.UserTypeService;




@RequiredArgsConstructor
@Service
public class UserTypeServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserTypeService {

    private final SysUserMapper sysUserMapper;

    public List<String> getRoleKeysByUserId(Long userId) {
        return sysUserMapper.selectRoleKeysByUserId(userId);
    }
}