package module.system.controller;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.system.dto.AccountDTO;
import module.system.dto.QueryUserInformationDTO;
import module.system.service.AdminService;
import module.system.vo.QueryInformationVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员业务接口管理器
 * 处理密码重置、账号冻结解冻等问题
 */
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/account")
    public Result getUserInformation(QueryUserInformationDTO dto){
        List<QueryInformationVo> listVo = adminService.getUserList(dto);
        return Result.success(listVo);

    }

    @GetMapping("/freeze")
    public Result freezeAccount(AccountDTO dto){
        adminService.freezeAccount(dto);
        return Result.success("冻结成功");
    }

}
