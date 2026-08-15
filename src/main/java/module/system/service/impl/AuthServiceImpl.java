package module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import common.exception.BusinessException;
import common.result.ResultCode;
import config.token.jwtTokenProvider;
import lombok.RequiredArgsConstructor;
import module.system.dto.loginDTO;
import module.system.entity.User;
import module.system.mapper.UserMapper;
import module.system.service.RedisService;
import module.system.vo.loginVo;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private final UserMapper userMapper;
    private final jwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    /**
     * 登录
     * @param loginDTO 用户登录参数
     * @return 登录成功返回的信息和令牌
     */
    @Override
    public loginVo login(loginDTO loginDTO) {

        //1.根据学号/教职工查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ID" , loginDTO.getUsername());

        //2.条件查询用户信息
        queryWrapper.select("username" , "real_name" , "user_type" , "password");
        User user = userMapper.selectOne(queryWrapper);

        //3.检验用户是否存在
        if(user == null){
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        //4.检验用户密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = user.getPassword();
        if(!encoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        //5.生成Token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername() , user.getUserType());
        String refreshToken = redisService.generateRefreshToken(user.getUsername());

        //6.构建返回
        return loginVo.builder()
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
}
