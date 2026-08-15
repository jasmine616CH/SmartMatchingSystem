package module.system.service;

import module.system.dto.logoutDTO;
import module.system.dto.tokenDTO;

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
    tokenDTO refreshAccessToken(String refreshToken);

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
    void logout(logoutDTO logoutDTO);

    /**
     * 删除用户的所有token（强制下线）
     * @param username 用户名
     */
    void deleteUserTokens(String username);

    /**
     * 检查accessToken是否有效
     * @param accessToken 访问令牌
     * @return true-有效，false-无效
     */
    boolean validateAccessToken(String accessToken);

    /**
     * 黑名单管理 - 将token加入黑名单
     * @param token 令牌
     * @param expiration 过期时间（秒）
     */
    void addToBlacklist(String token, long expiration);

    /**
     * 检查token是否在黑名单中
     * @param token 令牌
     * @return true-在黑名单中，false-不在
     */
    boolean isTokenBlacklisted(String token);

}
