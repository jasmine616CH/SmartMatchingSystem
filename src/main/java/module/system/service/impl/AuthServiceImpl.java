package module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import common.enums.UserType;
import common.exception.BusinessException;
import common.result.ResultCode;
import config.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import module.system.dto.RegisterDTO;
import module.system.dto.LoginDTO;
import module.system.entity.SysUser;
import module.system.mapper.SysUserMapper;
import module.system.service.AccountService;
import module.system.service.RedisService;
import module.system.vo.LoginVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final AccountService accountService;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 登录
     * @param loginDTO 用户登录参数
     * @return 登录成功返回的信息和令牌
     */
    @Override
    public LoginVo login(LoginDTO loginDTO) {

        //1.按登录账号查询（sys_user 的账号列是 username，原来写的 "ID" 这张表没有）
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginDTO.getUsername());
        // 不写 select 白名单：原实现漏选了 user_id，导致后续按 userId 查角色永远查不到

        //2.条件查询用户信息
        SysUser user = userMapper.selectOne(queryWrapper);

        //3.检验用户是否存在
        if(user == null){
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        //4.检验账号是否被冻结（status 是 Integer，直接 == 0 拆箱会 NPE）
        if (Integer.valueOf(0).equals(user.getStatus())){
            throw new BusinessException(ResultCode.USER_LOGOUT_FAIL);
        }

        //5.校验密码：用前端传的明文跟库里的哈希比。
        //  原实现把 user.getPassword() 同时当明文和哈希传进去，等于拿哈希跟哈希比，永远 false
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
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
     * 注册
     * @param registerDTO 用户注册参数
     */
    @Override
    public void register(RegisterDTO registerDTO) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhone, registerDTO.getPhone());
        SysUser existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 自助注册一律是方案工程师，角色由后端硬编码，RegisterDTO 不接受角色字段（杜绝提权）
        accountService.createAccount(
                registerDTO.getPhone(),          // 登录账号沿用手机号
                registerDTO.getRealName(),
                registerDTO.getPhone(),
                registerDTO.getEmail(),
                registerDTO.getPassword(),
                UserType.SELF_REGISTER_TYPE);
    }
}
