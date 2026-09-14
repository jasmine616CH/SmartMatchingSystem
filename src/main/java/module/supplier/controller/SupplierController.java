package module.supplier.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import common.enums.OperateModule;
import common.enums.OperateType;
import common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.supplier.dto.SupplierQueryDTO;
import module.supplier.dto.SupplierSaveDTO;
import module.supplier.dto.SupplierUpdateDTO;
import module.supplier.service.SupplierService;
import module.supplier.vo.SupplierDetailVO;
import module.supplier.vo.SupplierListVO;
import module.system.annotation.OperateLog;

/**
 * 供应商主体管理控制器
 * 实现供应商的增删改查与审核流转
 */
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * 分页查询供应商列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Page<SupplierListVO>> querySupplierList(
            @Valid SupplierQueryDTO dto) {
        return Result.success(supplierService.querySupplierList(dto));
    }

    /**
     * 查询供应商详情（含联系人列表）
     *
     * @param supplierId 供应商主键ID
     * @return 供应商详情
     */
    @GetMapping("/{supplierId}")
    public Result<SupplierDetailVO> querySupplierDetail(
            @NotNull(message = "supplierId不能为空") @PathVariable("supplierId") Long supplierId) {
        return Result.success(supplierService.querySupplierDetail(supplierId));
    }

    /**
     * 新增供应商
     *
     * @param dto 供应商信息
     * @return 新增结果
     */
    @OperateLog(operateDesc = "新增供应商", operateType = OperateType.ADD, operateModule = OperateModule.SUPPLIER)
    @PostMapping("")
    public Result<?> addSupplier(
            @Valid @RequestBody SupplierSaveDTO dto) {
        supplierService.addSupplier(dto);
        return Result.success();
    }

    /**
     * 修改供应商
     *
     * @param dto 供应商信息
     * @return 修改结果
     */
    @OperateLog(operateDesc = "修改供应商", operateType = OperateType.UPDATE, operateModule = OperateModule.SUPPLIER)
    @PutMapping("")
    public Result<?> updateSupplier(
            @Valid @RequestBody SupplierUpdateDTO dto) {
        supplierService.updateSupplier(dto);
        return Result.success();
    }

    /**
     * 删除供应商
     *
     * @param supplierId 供应商主键ID
     * @return 删除结果
     */
    @OperateLog(operateDesc = "删除供应商", operateType = OperateType.DELETE, operateModule = OperateModule.SUPPLIER)
    @DeleteMapping("/{supplierId}")
    public Result<?> deleteSupplier(
            @NotNull(message = "supplierId不能为空") @PathVariable("supplierId") Long supplierId) {
        supplierService.deleteSupplier(supplierId);
        return Result.success();
    }

    /**
     * 提交审核：草稿 → 待审核
     *
     * @param supplierId 供应商主键ID
     * @return 提交结果
     */
    @OperateLog(operateDesc = "提交供应商审核", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{supplierId}/submitAudit")
    public Result<?> submitAudit(
            @NotNull(message = "supplierId不能为空") @PathVariable("supplierId") Long supplierId) {
        supplierService.submitAudit(supplierId);
        return Result.success();
    }

    /**
     * 撤回：已发布 → 草稿
     *
     * @param supplierId 供应商主键ID
     * @return 撤回结果
     */
    @OperateLog(operateDesc = "撤回已发布的供应商", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{supplierId}/revoke")
    public Result<?> revoke(
            @NotNull(message = "supplierId不能为空") @PathVariable("supplierId") Long supplierId) {
        supplierService.revoke(supplierId);
        return Result.success();
    }

    /**
     * 撤销申请：待审核 → 草稿
     *
     * @param supplierId 供应商主键ID
     * @return 撤销结果
     */
    @OperateLog(operateDesc = "撤销供应商审核申请", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{supplierId}/cancelSubmit")
    public Result<?> cancelSubmit(
            @NotNull(message = "supplierId不能为空") @PathVariable("supplierId") Long supplierId) {
        supplierService.cancelSubmit(supplierId);
        return Result.success();
    }
}
