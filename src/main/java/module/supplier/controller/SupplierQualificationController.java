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
import module.supplier.dto.SupplierQualificationQueryDTO;
import module.supplier.dto.SupplierQualificationSaveDTO;
import module.supplier.dto.SupplierQualificationUpdateDTO;
import module.supplier.service.SupplierQualificationService;
import module.supplier.vo.SupplierQualificationVO;
import module.system.annotation.OperateLog;

/**
 * 供应商资质管理控制器
 * 实现供应商资质的增删改查与审核流转
 */
@RequestMapping("/api/supplier/qualification")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class SupplierQualificationController {

    private final SupplierQualificationService supplierQualificationService;

    /**
     * 分页查询供应商资质列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Page<SupplierQualificationVO>> queryQualificationList(
            @Valid SupplierQualificationQueryDTO dto) {
        return Result.success(supplierQualificationService.queryQualificationList(dto));
    }

    /**
     * 查询供应商资质详情
     *
     * @param qfId 资质主键ID
     * @return 资质详情
     */
    @GetMapping("/{qfId}")
    public Result<SupplierQualificationVO> queryQualificationDetail(
            @NotNull(message = "qfId不能为空") @PathVariable("qfId") Long qfId) {
        return Result.success(supplierQualificationService.queryQualificationDetail(qfId));
    }

    /**
     * 新增供应商资质
     *
     * @param dto 资质信息
     * @return 新增结果
     */
    @OperateLog(operateDesc = "新增供应商资质", operateType = OperateType.ADD, operateModule = OperateModule.SUPPLIER)
    @PostMapping("")
    public Result<?> addQualification(
            @Valid @RequestBody SupplierQualificationSaveDTO dto) {
        supplierQualificationService.addQualification(dto);
        return Result.success();
    }

    /**
     * 修改供应商资质
     *
     * @param dto 资质信息
     * @return 修改结果
     */
    @OperateLog(operateDesc = "修改供应商资质", operateType = OperateType.UPDATE, operateModule = OperateModule.SUPPLIER)
    @PutMapping("")
    public Result<?> updateQualification(
            @Valid @RequestBody SupplierQualificationUpdateDTO dto) {
        supplierQualificationService.updateQualification(dto);
        return Result.success();
    }

    /**
     * 删除供应商资质
     *
     * @param qfId 资质主键ID
     * @return 删除结果
     */
    @OperateLog(operateDesc = "删除供应商资质", operateType = OperateType.DELETE, operateModule = OperateModule.SUPPLIER)
    @DeleteMapping("/{qfId}")
    public Result<?> deleteQualification(
            @NotNull(message = "qfId不能为空") @PathVariable("qfId") Long qfId) {
        supplierQualificationService.deleteQualification(qfId);
        return Result.success();
    }

    /**
     * 提交审核：草稿 → 待审核
     *
     * @param qfId 资质主键ID
     * @return 提交结果
     */
    @OperateLog(operateDesc = "提交供应商资质审核", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{qfId}/submitAudit")
    public Result<?> submitAudit(
            @NotNull(message = "qfId不能为空") @PathVariable("qfId") Long qfId) {
        supplierQualificationService.submitAudit(qfId);
        return Result.success();
    }

    /**
     * 撤回：有效 → 草稿
     *
     * @param qfId 资质主键ID
     * @return 撤回结果
     */
    @OperateLog(operateDesc = "撤回供应商资质", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{qfId}/revoke")
    public Result<?> revoke(
            @NotNull(message = "qfId不能为空") @PathVariable("qfId") Long qfId) {
        supplierQualificationService.revoke(qfId);
        return Result.success();
    }

    /**
     * 撤销申请：待审核 → 草稿
     *
     * @param qfId 资质主键ID
     * @return 撤销结果
     */
    @OperateLog(operateDesc = "撤销供应商资质审核申请", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{qfId}/cancelSubmit")
    public Result<?> cancelSubmit(
            @NotNull(message = "qfId不能为空") @PathVariable("qfId") Long qfId) {
        supplierQualificationService.cancelSubmit(qfId);
        return Result.success();
    }
}
