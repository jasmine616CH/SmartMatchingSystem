package module.system.controller;

import common.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import module.system.dto.*;
import module.system.service.AuthService;
import module.system.service.RedisService;
import module.system.vo.LoginVO;
import module.system.vo.tokenRefreshVo;
import org.springframework.web.bind.annotation.*;
import common.result.Result;

/**
 * 系统登录业务控制器
 */
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RedisService redisService;

    /**
     * 用户登录接口
     *
     * @param loginDTO 登录请求参数
     * @return 成功返回相关参数，失败返回错误信息
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(@Valid @RequestBody TokenLogoutDTO request) {
        try {
            redisService.logout(LogoutDTO.builder()
                    .accessToken(request.getAccessToken())
                    .build());
            return Result.success();
        } catch (Exception e) {
            log.error("登出异常", e);
            return Result.error(40404, "登出失败");
        }
    }


    /**
     * 用户注册接口
     * @param request
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO request) {
        authService.register(request);
        return Result.success();
    }

    /**
     * 刷新token接口
     */
    @PostMapping("/refresh-token")
    public Result<tokenRefreshVo> refreshToken(
            @Valid @RequestBody TokenRefreshDTO request) {

        try {
            // 1. 验证refreshToken
            if (!redisService.validateRefreshToken(request.getRefreshToken())) {
                return Result.error(401, "刷新令牌无效或已过期，请重新登录");
            }

            // 2. 刷新token
            TokenDTO tokenDTO = redisService.refreshAccessToken(
                    request.getRefreshToken());

            // 3. 构建响应
            tokenRefreshVo vo = tokenRefreshVo.builder()
                    .accessToken(tokenDTO.getAccessToken())
                    .refreshToken(tokenDTO.getRefreshToken())
                    .expiresIn(tokenDTO.getExpiresIn())
                    .refreshExpiresIn(tokenDTO.getRefreshExpiresIn())
                    .tokenType(tokenDTO.getTokenType())
                    .build();

            return Result.success(vo);

        } catch (BusinessException e) {
            // 处理刷新异常
            log.warn("Token刷新失败：{}", e.getMessage());
            return Result.error(40005, "Token刷新失败");
        } catch (Exception e) {
            log.error("Token刷新异常", e);
            return Result.error(40006, "Token刷新异常");
        }
    }
}
