package module.system.service.impl;

import com.alibaba.excel.util.StringUtils;
import common.exception.BusinessException;
import common.result.ResultCode;
import config.token.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import module.system.dto.LogoutDTO;
import module.system.dto.TokenDTO;
import module.system.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 系统缓存业务接口实现类
 * 处理令牌管理相关操作
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String USER_TOKEN_PREFIX = "user:token:";
    // 黑名单相关常量已移除
    private static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60; // 7天
    private static final long ACCESS_TOKEN_EXPIRE = 30 * 60; // 30分钟

    @Override
    public String generateRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        String key = REFRESH_TOKEN_PREFIX + refreshToken;

        // 存储refreshToken，关联用户信息
        Map<String, String> tokenInfo = new HashMap<>();
        tokenInfo.put("username", username);
        tokenInfo.put("createTime", String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForHash().putAll(key, tokenInfo);
        redisTemplate.expire(key, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);

        log.info("生成刷新令牌成功，用户：{}", username);
        return refreshToken;
    }

    @Override
    public TokenDTO refreshAccessToken(String refreshToken) {
        // 1. 验证refreshToken是否存在
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        }

        // 2. 获取用户信息（黑名单检查已移除）
        Map<Object, Object> tokenInfo = redisTemplate.opsForHash().entries(key);
        String username = (String) tokenInfo.get("username");
        String userType = (String) tokenInfo.get("userType");
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 3. 删除旧的refreshToken（不再加入黑名单）
        redisTemplate.delete(key);

        // 4. 生成新的token对
        String newAccessToken = jwtTokenProvider.generateAccessToken(username, userType);
        String newRefreshToken = generateRefreshToken(username);

        // 5. 保存新的refreshToken到用户关联
        saveUserTokenMapping(username, newRefreshToken);

        log.info("刷新令牌成功，用户：{}，旧refreshToken已失效", username);

        return TokenDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(ACCESS_TOKEN_EXPIRE)
                .refreshExpiresIn(REFRESH_TOKEN_EXPIRE)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        // 只检查是否存在，黑名单检查已移除
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void logout(LogoutDTO logoutDTO) {
        String accessToken = logoutDTO.getAccessToken();
        String refreshToken = logoutDTO.getRefreshToken();

        // 1. 处理accessToken：仅删除用户映射，不加入黑名单
        if (StringUtils.isNotBlank(accessToken)) {
            String username = jwtTokenProvider.getUserNameFromToken(accessToken);
            if (StringUtils.isNotBlank(username)) {
                redisTemplate.delete(USER_TOKEN_PREFIX + username);
            }
        }

        // 2. 处理refreshToken：删除其key，不加入黑名单
        if (StringUtils.isNotBlank(refreshToken)) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
        }

        log.info("用户登出成功，accessToken: {}, refreshToken: {}",
                accessToken != null, refreshToken != null);
    }

    @Override
    public void deleteUserTokens(String username) {
        String key = USER_TOKEN_PREFIX + username;
        Set<String> tokens = redisTemplate.opsForSet().members(key);
        if (tokens != null) {
            for (String token : tokens) {
                // 仅删除refreshToken key，不加入黑名单
                redisTemplate.delete(REFRESH_TOKEN_PREFIX + token);
            }
        }
        redisTemplate.delete(key);
        log.info("强制下线用户：{}，已清除所有token", username);
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }
        // 黑名单检查已移除，仅验证JWT本身是否有效
        return jwtTokenProvider.isTokenValid(accessToken);
    }

    @Override
    public void addToBlacklist(String token, long expiration) {
        // 方法保留但实现为空（不再加入黑名单）
        log.debug("黑名单功能已禁用，Token不会被加入黑名单：{}", token);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        // 方法保留但始终返回false（不再检查黑名单）
        return false;
    }

    // 辅助方法：保存用户与token的关联
    private void saveUserTokenMapping(String username, String refreshToken) {
        String key = USER_TOKEN_PREFIX + username;
        redisTemplate.opsForSet().add(key, refreshToken);
        redisTemplate.expire(key, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
    }
}