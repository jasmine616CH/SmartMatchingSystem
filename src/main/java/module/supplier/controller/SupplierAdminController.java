package module.supplier.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.supplier.dto.AdminSupplierDTO;
import module.supplier.dto.QuerySupplierDTO;
import module.supplier.service.SupplierAdminService;
import module.supplier.vo.QuerySupplierVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 供应商管理员业务接口控制器
 * 实现对供应商账号的管理
 */
@RequestMapping("/supplier/admin")
@RequiredArgsConstructor
@RestController
@Slf4j
public class SupplierAdminController {

    @Autowired
    private SupplierAdminService supplierAdminService;

    /**
     * 查找用户
     * @param dto 用户 查找信息
     * @return 用户列表
     */
    @GetMapping("/account")
    public Result<Page<QuerySupplierVo>> getUserInformation(QuerySupplierDTO dto){
        Page<QuerySupplierVo> listVo = supplierAdminService.getSupplierList(dto);
        return Result.success(listVo);

    }

    /**
     * 新增供应商
     * @param dto 账号信息
     * @return 成功返回相关信息
     */
    @PostMapping("/add-supplier")
    public Result<String> addAccount(AdminSupplierDTO dto){
        supplierAdminService.addSupplierAccount(dto);
        return Result.success("增添成功");
    }

}
