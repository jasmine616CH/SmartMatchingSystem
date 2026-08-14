package module.system.service;

import module.system.dto.loginDTO;
import module.system.dto.logoutDTO;
import module.system.vo.loginVo;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 系统业务接口
 * 实现用户登录和登出
 */
public interface AuthService {

    /**
     * 登录
     * @param loginDTO 用户登录参数
     * @return 登录成功返回的信息和令牌
     */
    loginVo login(loginDTO loginDTO);

    /**
     * 登出
     * @param http 登出系统参数
     */
    SecurityFilterChain logOut(HttpSecurity http) throws Exception;
}
