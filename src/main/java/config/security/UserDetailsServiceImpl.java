package config.security;

import module.system.entity.SysUser;
import module.system.service.SysUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        // 根据用户名查询数据库用户
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        SysUser sysUser = sysUserService.getOne(queryWrapper);

        // 查询不到抛出安全异常
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        LoginUser loginUser = new LoginUser(sysUser);
        loginUser.setRoleKeys(sysUserService.getRoleKeysByUserId(sysUser.getUserId()));

        // 包装为 LoginUser
        return loginUser;
    }
}