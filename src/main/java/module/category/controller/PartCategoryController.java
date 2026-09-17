package module.category.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.enums.OperateModule;
import common.enums.OperateType;
import common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.category.dto.PartCategorySaveDTO;
import module.category.service.PartCategoryService;
import module.category.vo.PartCategoryDetailVO;
import module.category.vo.PartCategoryTreeVO;
import module.system.annotation.OperateLog;

/**
 * 模板体系管理控制器
 */
@RequestMapping("/api/category")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartCategoryController {

    private final PartCategoryService partCategoryService;

    /**
     * 查询配件分类树
     * 
     * @return
     * @author 徐宝福
     */
    @GetMapping("/tree")
    public Result<List<PartCategoryTreeVO>> queryCategoryTree() {
        return Result.success(partCategoryService.queryCategoryTree());
    }

    /**
     * 查询配件分类详情
     * 
     * @param catId
     * @return
     * @author 徐宝福
     */
    @GetMapping("/{catId}")
    public Result<PartCategoryDetailVO> queryCategoryDetail(
        @NotNull(message = "catId 不能为空") @PathVariable Long catId) {
        return Result.success(partCategoryService.queryCategoryDetail(catId));
    }

    /**
    * 新增配件分类接口
    * @param partCategorySaveDTO
    * 
    * @return
    * @author 徐宝福
    */
    @PostMapping("")
    public Result<?> addCategory(
        @Valid @RequestBody PartCategorySaveDTO partCategorySaveDTO) {
        partCategoryService.addCategory(partCategorySaveDTO);
        return Result.success();
    }

    /**
     * 修改配件分类接口
     * 
     * @param partCategorySaveDTO
     * @return
     * @author 徐宝福
     */
    @PutMapping("/update")
    public Result<?> updateCategory(
        @Valid @RequestBody PartCategorySaveDTO partCategorySaveDTO) {
        partCategoryService.updateCategory(partCategorySaveDTO);
        return Result.success();
    }

    /**
     * 删除配件分类接口
     * 
     * @param catId
     * @return
     * @author 徐宝福
     */
    @DeleteMapping("/{catId}")
    public Result<?> deleteCategory(
        @NotNull(message = "catId 不能为空") @PathVariable Long catId) {
        partCategoryService.deleteCategory(catId);
        return Result.success();
    }

    /**
     * 提交审核：草稿 → 待审核
     *
     * @param catId 分类主键ID
     * @return 提交结果
     */
    @OperateLog(operateDesc = "提交配件分类审核", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{catId}/submitAudit")
    public Result<?> submitAudit(
            @NotNull(message = "catId不能为空") @PathVariable("catId") Long catId) {
        partCategoryService.submitAudit(catId);
        return Result.success();
    }

    /**
     * 撤回：已发布 → 草稿
     *
     * @param catId 分类主键ID
     * @return 撤回结果
     */
    @OperateLog(operateDesc = "撤回已发布的配件分类", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{catId}/revoke")
    public Result<?> revoke(
            @NotNull(message = "catId不能为空") @PathVariable("catId") Long catId) {
        partCategoryService.revoke(catId);
        return Result.success();
    }

    /**
     * 撤销申请：待审核 → 草稿
     *
     * @param catId 分类主键ID
     * @return 撤销结果
     */
    @OperateLog(operateDesc = "撤销配件分类审核申请", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/{catId}/cancelSubmit")
    public Result<?> cancelSubmit(
            @NotNull(message = "catId不能为空") @PathVariable("catId") Long catId) {
        partCategoryService.cancelSubmit(catId);
        return Result.success();
    }
}
