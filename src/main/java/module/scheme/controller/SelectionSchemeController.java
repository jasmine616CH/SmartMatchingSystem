package module.scheme.controller;

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
import module.scheme.dto.SchemePartQuantityDTO;
import module.scheme.dto.SchemePartQueryDTO;
import module.scheme.dto.SchemeQueryDTO;
import module.scheme.dto.SchemeSaveSelectionDTO;
import module.scheme.dto.SchemeUpdateDTO;
import module.scheme.service.SelectionSchemeService;
import module.scheme.vo.SchemeBriefVO;
import module.scheme.vo.SchemeDetailVO;
import module.scheme.vo.SchemePartVO;
import module.scheme.vo.SchemeSummaryVO;
import module.scheme.vo.SchemeValidateResultVO;
import module.system.annotation.OperateLog;

/**
 * 选型方案管理控制器
 * 实现方案的新增、查询、修改、删除、明细维护与参数校验
 */
@RequestMapping("/api/scheme")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class SelectionSchemeController {

    private final SelectionSchemeService selectionSchemeService;

    /**
     * 保存选配结果为方案
     *
     * @param dto 选配结果
     * @return 新建的方案ID
     */
    @OperateLog(operateDesc = "保存选配结果为方案", operateType = OperateType.ADD, operateModule = OperateModule.SCHEME)
    @PostMapping("/save-selection")
    public Result<Long> saveSelection(
            @Valid @RequestBody SchemeSaveSelectionDTO dto) {
        return Result.success(selectionSchemeService.saveSelection(dto));
    }

    /**
     * 分页查询方案列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Page<SchemeBriefVO>> querySchemeList(
            @Valid SchemeQueryDTO dto) {
        return Result.success(selectionSchemeService.querySchemeList(dto));
    }

    /**
     * 查询方案详情
     *
     * @param schemeId 方案主键ID
     * @return 方案详情
     */
    @GetMapping("/{schemeId}")
    public Result<SchemeDetailVO> querySchemeDetail(
            @NotNull(message = "schemeId不能为空") @PathVariable("schemeId") Long schemeId) {
        return Result.success(selectionSchemeService.querySchemeDetail(schemeId));
    }

    /**
     * 分页查询方案配件明细
     *
     * @param schemeId 方案主键ID
     * @param dto      查询条件
     * @return 分页结果
     */
    @GetMapping("/{schemeId}/part/page")
    public Result<Page<SchemePartVO>> querySchemePartPage(
            @NotNull(message = "schemeId不能为空") @PathVariable("schemeId") Long schemeId,
            @Valid SchemePartQueryDTO dto) {
        return Result.success(selectionSchemeService.querySchemePartPage(schemeId, dto));
    }

    /**
     * 修改方案基本信息
     *
     * @param dto 方案信息
     * @return 修改结果
     */
    @OperateLog(operateDesc = "修改选型方案", operateType = OperateType.UPDATE, operateModule = OperateModule.SCHEME)
    @PutMapping("")
    public Result<?> updateScheme(
            @Valid @RequestBody SchemeUpdateDTO dto) {
        selectionSchemeService.updateScheme(dto);
        return Result.success();
    }

    /**
     * 删除方案
     *
     * @param schemeId 方案主键ID
     * @return 删除结果
     */
    @OperateLog(operateDesc = "删除选型方案", operateType = OperateType.DELETE, operateModule = OperateModule.SCHEME)
    @DeleteMapping("/{schemeId}")
    public Result<?> deleteScheme(
            @NotNull(message = "schemeId不能为空") @PathVariable("schemeId") Long schemeId) {
        selectionSchemeService.deleteScheme(schemeId);
        return Result.success();
    }

    /**
     * 批量修改明细数量
     *
     * @param schemeId 方案主键ID
     * @param dto      明细数量
     * @return 修改后的方案汇总
     */
    @OperateLog(operateDesc = "修改方案明细数量", operateType = OperateType.UPDATE, operateModule = OperateModule.SCHEME)
    @PutMapping("/{schemeId}/parts")
    public Result<SchemeSummaryVO> updatePartQuantity(
            @NotNull(message = "schemeId不能为空") @PathVariable("schemeId") Long schemeId,
            @Valid @RequestBody SchemePartQuantityDTO dto) {
        return Result.success(selectionSchemeService.updatePartQuantity(schemeId, dto));
    }

    /**
     * 删除方案中的配件明细
     *
     * @param schemeId     方案主键ID
     * @param schemePartId 明细主键ID
     * @return 删除后的方案汇总
     */
    @OperateLog(operateDesc = "删除方案配件明细", operateType = OperateType.DELETE, operateModule = OperateModule.SCHEME)
    @DeleteMapping("/{schemeId}/parts/{schemePartId}")
    public Result<SchemeSummaryVO> deleteSchemePart(
            @NotNull(message = "schemeId不能为空") @PathVariable("schemeId") Long schemeId,
            @NotNull(message = "schemePartId不能为空") @PathVariable("schemePartId") Long schemePartId) {
        return Result.success(selectionSchemeService.deleteSchemePart(schemeId, schemePartId));
    }

    /**
     * 方案参数合法性校验
     *
     * @param schemeId 方案主键ID
     * @return 校验结果
     */
    @PostMapping("/{schemeId}/validate")
    public Result<SchemeValidateResultVO> validateScheme(
            @NotNull(message = "schemeId不能为空") @PathVariable("schemeId") Long schemeId) {
        return Result.success(selectionSchemeService.validateScheme(schemeId));
    }
}
