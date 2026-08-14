package module.system.controller;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.system.dto.signInDTO;
import module.system.service.ApplyService;
import module.system.vo.signInVo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统申请账号业务控制器
 * 实现系统的申请账号
 */
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
@Slf4j
public class ApplyController {

    private final ApplyService applyService;

    /**
     * 用户注册接口
     * @param signInVo 注册请求参数
     * @return 成功返回统一结果，错误返回错误信息
     */
    @PostMapping("/register")
    public Result register(@RequestBody signInVo signInVo){

        //vo转dto
        signInDTO signInDTO = new signInDTO();
        BeanUtils.copyProperties(signInVo, signInDTO);

        applyService.register(signInDTO);
        return Result.success();
    }

}
