package module.system.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import module.system.dto.loginDTO;
import module.system.service.AuthService;
import module.system.service.redisService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.bind.annotation.*;
import common.result.Result;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final redisService redisService;

    /**
     * 用户登录接口
     *
     * @param loginDTO 登录请求参数
     * @return 成功返回相关参数，失败返回错误信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody loginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    /**
     * 用户登出接口
     * @param http 登出相关参数
     * @return  成功返回相关参数，失败返回错误信息
     */
    @PostMapping("/logout")
    public Result logOut(HttpSecurity http){
        try {
            authService.logOut(http);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Result.success();
    }

    /**
     * 刷新token接口
     * @param refreshToken 刷新token
     * @return 成功返回accessToken和refreshToken,失败返回错误信息
     */
    @PostMapping("/refresh-access-token")
    public Result refreshAccessToken(@NotBlank @RequestParam String refreshToken){
        return Result.success(redisService.refreshAccessToken(refreshToken));
    }

}
