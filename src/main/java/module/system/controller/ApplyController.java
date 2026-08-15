package module.system.controller;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.system.dto.signInDTO;
import module.system.service.ApplyService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统申请账号业务控制器
 * 实现方案工程师的申请账号
 */
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
@Slf4j
public class ApplyController {

    private final ApplyService applyService;

    /**
     * 用户注册接口
     *
     * @param signInDTO 方案工程师注册请求参数
     * @return 成功返回统一结果，错误返回错误信息
     */
    @PostMapping("/register")
    public Result register(@RequestBody signInDTO signInDTO){

        //1.注册密码进行加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = signInDTO.getPassword();
        signInDTO.setPassword(encoder.encode(rawPassword));

        //2.设置用户权限（方案工程师）和用户名(手机号）
        signInDTO.setUserType("solution_engineer");
        signInDTO.setUsername(signInDTO.getPhone());

        applyService.register(signInDTO);
        return Result.success();
    }

}
