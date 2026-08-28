package module.template.controller;

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

import common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.template.dto.PartCategorySaveDTO;
import module.template.service.PartCategoryService;
import module.template.vo.PartCategoryDetailVO;
import module.template.vo.PartCategoryTreeVO;

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
     */
    @DeleteMapping("/{catId}")
    public Result<?> deleteCategory(
        @NotNull(message = "catId 不能为空") @PathVariable Long catId) {
        partCategoryService.deleteCategory(catId);
        return Result.success();
    }
}
