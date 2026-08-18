package module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import common.exception.BusinessException;
import common.result.ResultCode;
import config.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import module.system.dto.RegisterDTO;
import module.system.dto.LoginDTO;
import module.system.entity.SysUser;
import module.system.mapper.SysUserMapper;
import module.system.service.RedisService;
import module.system.vo.LoginVo;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import module.system.service.AuthService;

/**
 * 系统业务实现类
 * 实现用户登录和登出
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 登录
     * @param loginDTO 用户登录参数
     * @return 登录成功返回的信息和令牌
     */
    @Override
    public LoginVo login(LoginDTO loginDTO) {

        //1.根据学号/教职工查询
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ID" , loginDTO.getUsername());

        //2.条件查询用户信息
        queryWrapper.select("username" , "real_name" , "user_type" , "password" , "status");
        SysUser user = userMapper.selectOne(queryWrapper);

        //3.检验用户是否存在
        if(user == null){
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        //4.检验用户是否在黑名单
        if (user.getStatus() == 0){
            throw new BusinessException(ResultCode.USER_LOGOUT_FAIL);
        }

        //5.检验用户密码
        String rawPassword = user.getPassword();
        if(!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        //6.生成Token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername() , user.getUserType());
        String refreshToken = redisService.generateRefreshToken(user.getUsername());

        //7.构建返回
        return LoginVo.builder()
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(1800L)           // 30分钟
                .refreshExpiresIn(604800L)  // 7天
                .tokenType("Bearer")
                .build();
    }

    /**
     * 登出
     * @param http 返回登出参数
     */
    @Override
    @Bean
    public SecurityFilterChain logOut(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/api/auth/login")
                        .defaultSuccessUrl("/api/auth/login", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")                // 登出请求地址
                        .logoutSuccessUrl("/api/auth/login")   // 登出成功后跳转到登录页，并携带参数
                        .invalidateHttpSession(true)         // 清除 Session
                        .deleteCookies("JSESSIONID")         // 删除浏览器会话 Cookie
                        .permitAll()
                );
        return http.build();
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, registerDTO.getPhone());
        SysUser existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        SysUser user = SysUser.builder()
                .username(registerDTO.getPhone())
                .realName(registerDTO.getRealName())
                .phone(registerDTO.getPhone())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .build();

        userMapper.insert(user);
    }
}
