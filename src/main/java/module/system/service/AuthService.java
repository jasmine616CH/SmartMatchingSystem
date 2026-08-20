package module.system.service;

import module.system.dto.RegisterDTO;
import module.system.dto.LoginDTO;
import module.system.vo.LoginVo;

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
    LoginVo login(LoginDTO loginDTO);

    /**
     * 注册
     * @param registerDTO 用户注册参数
     */
    void register(RegisterDTO registerDTO);
}
