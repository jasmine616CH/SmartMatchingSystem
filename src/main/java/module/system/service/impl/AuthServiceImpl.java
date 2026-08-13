package module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import common.exception.BusinessException;
import common.result.ResultCode;
import module.system.dto.loginDTO;
import module.system.entity.User;
import module.system.mapper.UserMapper;
import module.system.vo.loginVo;
import org.springframework.stereotype.Service;

import module.system.service.AuthService;

/**
 * 系统业务实现类
 * 实现用户登录和登出
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

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
        queryWrapper.select("userName" , "name" , "permission" , "passwordHash");
        User user = userMapper.selectOne(queryWrapper);

        //3.检验用户是否存在
        if(user == null){
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        //4.检验用户密码
        if(!aesService.decrypt(logInDTO.getPasswordHash()).equals(user.getPassword())){
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        //5.生成Token
        String accessToken = jwtTokenProvider.generateAccessToken(user.getID() , user.getUsername() , user.getPermission());
        String refreshToken = redisService.generateRefreshToken(user.getID());

        //6.封装登录信息
        loginVo logInVo = new loginVo();
        logInVo.setAccessToken(accessToken);
        logInVo.setRefreshToken(refreshToken);
        logInVo.setUsername(user.getUsername());
        logInVo.setName(user.getName());
        logInVo.setPermission(user.getPermission());

        return logInVo;
    }
}
