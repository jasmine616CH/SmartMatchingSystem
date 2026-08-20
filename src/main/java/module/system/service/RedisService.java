package module.system.service;

import module.system.dto.LogoutDTO;
import module.system.dto.TokenDTO;

/**
 * 缓存业务接口
 * 处理令牌管理相关操作
 */
public interface RedisService {

    /**
     * 生成刷新Token
     * @param username 用户名
     * @return 刷新令牌
     */
    String generateRefreshToken(String username);

    /**
     * 刷新访问令牌
     * @param refreshToken 刷新令牌
     * @return 令牌对（包含accessToken和refreshToken）
     */
    TokenDTO refreshAccessToken(String refreshToken);

    /**
     * 验证刷新令牌是否有效
     * @param refreshToken 刷新令牌
     * @return true-有效，false-无效
     */
    boolean validateRefreshToken(String refreshToken);

    /**
     * 用户登出
     * @param logoutDTO 登出参数
     */
    void logout(LogoutDTO logoutDTO);


}
