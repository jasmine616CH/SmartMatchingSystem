package module.part.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import common.enums.OperateModule;
import common.enums.OperateType;
import common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.part.dto.PartInfoSaveDTO;
import module.part.dto.PartInfoUpdateDTO;
import module.part.service.PartInfoService;
import module.part.vo.PartInfoDetailVO;
import module.part.vo.PartInfoListVO;
import module.system.annotation.OperateLog;

/**
 * 配件信息管理控制器
 */
@RequestMapping("/api/part")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartInfoController {

    private final PartInfoService partInfoService;

    /**
     * 获取配件信息列表
     * 
     * @param catId
     * @param pageNum
     * @param pageSize
     * @return
     * @author 徐宝福
     */
    @GetMapping("/{catId}/list")
    public Result<Page<PartInfoListVO>> queryPartInfoList(
            @NotNull(message = "catId不能为空") @PathVariable("catId") Long catId,
            @NotNull(message = "pageNum不能为空") @RequestParam(defaultValue = "1") Integer pageNum,
            @NotNull(message = "pageSize不能为空") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(partInfoService.queryPartInfoList(catId, pageNum, pageSize));
    }

    /**
     * 查询配件档案详情
     *
     * @param partId 配件主键ID
     * @return 配件详情
     * @author 徐宝福
     */
    @GetMapping("/{partId}")
    public Result<PartInfoDetailVO> queryPartInfoDetail(
            @NotNull(message = "partId不能为空") @PathVariable("partId") Long partId) {
        return Result.success(partInfoService.queryPartInfoDetail(partId));
    }

    /**
     * 新增配件信息
     * 
     * @param partInfoDTO
     * @return
     * @author 徐宝福
     */
    @OperateLog(operateDesc = "新增配件信息", operateType = OperateType.ADD, operateModule = OperateModule.PART)
    @PostMapping("")
    public Result<?> addPartInfo(
            @Valid @RequestBody PartInfoSaveDTO partInfoSaveDTO) {
        partInfoService.addPartInfo(partInfoSaveDTO);
        return Result.success();
    }

    /**
     * 修改配件信息
     * 
     * @param partId
     * @param partInfoDTO
     * @return
     * @author 徐宝福
     */
    @OperateLog(operateDesc = "修改配件信息", operateType = OperateType.UPDATE, operateModule = OperateModule.PART)
    @PutMapping("")
    public Result<?> updatePartInfo(
            @Valid @RequestBody PartInfoUpdateDTO partInfoUpdateDTO) {
        partInfoService.updatePartInfo(partInfoUpdateDTO);
        return Result.success();
    }

    /**
     * 删除配件信息
     * 
     * @param partId
     * @return
     * @author 徐宝福
     */
    @OperateLog(operateDesc = "删除配件信息", operateType = OperateType.DELETE, operateModule = OperateModule.PART)
    @DeleteMapping("/{partId}")
    public Result<?> deletePartInfo(
            @NotNull(message = "partId不能为空") @PathVariable("partId") Long partId) {
        partInfoService.deletePartInfo(partId);
        return Result.success();
    }

    /**
     * 提交审核：草稿 → 待审核
     *
     * @param partId 配件主键ID
     * @return 提交结果
     * @author 徐宝福
     */
    @OperateLog(operateDesc = "提交配件审核", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{partId}/submitAudit")
    public Result<?> submitAudit(
            @NotNull(message = "partId不能为空") @PathVariable("partId") Long partId) {
        partInfoService.submitAudit(partId);
        return Result.success();
    }

    /**
     * 撤回：已发布 → 草稿
     *
     * @param partId 配件主键ID
     * @return 撤回结果
     * @author 徐宝福
     */
    @OperateLog(operateDesc = "撤回已发布的配件", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{partId}/revoke")
    public Result<?> revoke(
            @NotNull(message = "partId不能为空") @PathVariable("partId") Long partId) {
        partInfoService.revoke(partId);
        return Result.success();
    }

    /**
     * 撤销申请：待审核 → 草稿
     *
     * @param partId 配件主键ID
     * @return 撤销结果
     * @author 徐宝福
     */
    @OperateLog(operateDesc = "撤销配件审核申请", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{partId}/cancelSubmit")
    public Result<?> cancelSubmit(
            @NotNull(message = "partId不能为空") @PathVariable("partId") Long partId) {
        partInfoService.cancelSubmit(partId);
        return Result.success();
    }
}
