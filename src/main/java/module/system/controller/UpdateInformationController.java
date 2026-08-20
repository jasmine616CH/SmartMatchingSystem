package module.system.controller;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.system.dto.PasswordUpdateDTO;
import module.system.service.UpdateInformationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 修改信息业务接口控制器
 * 实现用户信息修改功能
 */
@RequestMapping("/profile")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UpdateInformationController {

    private final UpdateInformationService updateInformationService;

    /**
     * 修改用户密码
     *
     * @param dto 用户信息
     * @return 成功返回相关信息
     */
    @PostMapping("/update")
    public Result update(PasswordUpdateDTO dto){
        updateInformationService.updateInformation(dto);
        return Result.success("修改成功");
    }

}
