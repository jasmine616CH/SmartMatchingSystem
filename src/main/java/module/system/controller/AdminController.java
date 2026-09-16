package module.system.controller;

import common.result.Result;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.system.dto.AccountDTO;
import module.system.dto.AddAccountDTO;
import module.system.dto.QueryUserInformationDTO;
import module.system.service.AdminService;
import module.system.vo.QueryAccountVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员业务接口管理器
 * 处理密码重置、账号冻结解冻等问题
 */
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AdminController {

    private final AdminService adminService;

    /**
     * 查找用户
     * @param dto 用户 查找信息
     * @return 用户列表
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/account")
    public Result<List<QueryAccountVo>> getUserInformation(QueryUserInformationDTO dto){
        List<QueryAccountVo> listVo = adminService.getUserList(dto);
        return Result.success(listVo);

    }

    /**
     * 冻结账户
     * @param dto 账户信息
     * @return 返回成功相关信息
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/freeze")
    public Result<String> freezeAccount(AccountDTO dto){
        adminService.freezeAccount(dto);
        return Result.success("冻结成功");
    }

    /**
     * 解冻账户
     * @param dto 账户信息
     * @return 返回成功相关信息
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/unfreeze")
    public Result<String> unfreezeAccount(AccountDTO dto){
        adminService.unfreezeAccount(dto);
        return Result.success("解冻成功");
    }

    /**
     * 重置密码
     * @param dto 账户信息
     * @return 返回密码
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/reset-password")
    public Result<String> resetPassword(AccountDTO dto){
        return Result.success(adminService.resetPassword(dto));
    }

    /**
     * 新增用户
     * @param dto 账号信息
     * @return 成功返回相关信息
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    // 注意：其余几个端点都是 GET + query 参数，这里是 POST，
    // 原来漏了 @RequestBody，导致前端发 JSON 时参数绑不上（全部为 null）。
    @PostMapping("/add-account")
    public Result<String> addAccount(@Valid @RequestBody AddAccountDTO dto){
        adminService.addNewAccount(dto);
        return Result.success("增添成功");
    }

}
