package module.system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import common.result.Result;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
@Slf4j
public class AuthController {

    /**
     * 用户登录接口
     *
     * @param loginDTO 登录请求参数
     * @return 成功返回相关参数，失败返回错误信息
     */
    @PostMapping("/login")
    public Result login() {
        return Result.success();
    }

}
