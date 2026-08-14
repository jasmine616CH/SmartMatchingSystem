package module.system.service;

import java.util.Map;

/**
 * 缓存业务接口
 * 处理令牌管理相关操作
 */
public interface redisService {

    /**
     * 生成刷新Token
     * @param username 用户ID
     * @return 刷新令牌
     */
    String generateRefreshToken(String username);

    /**
     * 刷新访问令牌
     * @param refreshToken 刷新令牌
     * @return  令牌对
     */
    Map<String , String> refreshAccessToken(String refreshToken);
}
