package module.system.service;

import module.system.dto.loginDTO;
import module.system.vo.loginVo;

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
}
