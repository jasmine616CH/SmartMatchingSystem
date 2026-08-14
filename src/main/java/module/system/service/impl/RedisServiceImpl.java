package module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.jwtTokenProvider;
import lombok.RequiredArgsConstructor;
import module.system.dto.logoutDTO;
import module.system.entity.User;
import module.system.mapper.UserMapper;
import module.system.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 系统缓存业务接口实现类
 * 处理令牌管理相关操作
 */
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final jwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserMapper userMapper;

    @Value("${token.refresh-token-ttl}")
    private long refreshTokenTtl;

    //redis key 前缀
    private static final String USER_RT_PREFIX = "refresh-token:user:";
    private static final String RT_PREFIX = "refresh-token:rt:";

    /**
     *生成刷新令牌
     * @param username 用户用户名
     * @return 新令牌
     */
    @Override
    public String generateRefreshToken(String username) {

        //1.删除旧的refreshToken
        String oldRefreshToken = stringRedisTemplate.opsForValue().get(USER_RT_PREFIX + username);
        if (oldRefreshToken != null){
            stringRedisTemplate.delete(RT_PREFIX + oldRefreshToken);
        }

        //2.生成新的refreshToken
        String refreshToken = UUID.randomUUID().toString();

        //3.保存双向映射
        stringRedisTemplate.opsForValue()
                .set(RT_PREFIX + refreshToken , String.valueOf(username) , refreshTokenTtl , TimeUnit.HOURS);
        stringRedisTemplate.opsForValue()
                .set(RT_PREFIX + username , refreshToken , refreshTokenTtl , TimeUnit.HOURS);

        return refreshToken;
    }

    /**
     * 使用 refreshToken 刷新 accessToken
     * @param refreshToken 刷新令牌
     */
    @Override
    public Map<String, String> refreshAccessToken(String refreshToken) {

        //1.检验令牌是否有效
        String username = stringRedisTemplate.opsForValue().get(RT_PREFIX + refreshToken);
        if (username == null){
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        //2.查询用户信息
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery()
                .eq(User::getUsername , username)
                .select(User::getUsername , User::getPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        //3.生成新的accessToken
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUsername() , user.getUserType());

        //4.删除旧的token 生成新的token
        revokeRefreshToken(username , refreshToken);
        String newRefreshToken = generateRefreshToken(user.getUsername());

        //5.返回令牌
        Map<String , String> map = new HashMap<>();
        map.put("refreshToken" , newRefreshToken);
        map.put("accessToken" , newAccessToken);
        return map;
    }


    /**
     * 登出
     * @param logoutDTO 登出参数
     */
    @Override
    public void logOut(logoutDTO logoutDTO) {
        //撤销刷新令牌
        revokeRefreshToken(logoutDTO.getUserID() , logoutDTO.getRefreshToken());

    }

    /**
     * 主动撤销token
     */
    public void revokeRefreshToken(String username, String refreshToken){
        stringRedisTemplate.delete(USER_RT_PREFIX + username);
        stringRedisTemplate.delete(RT_PREFIX + refreshToken);
    }

}
