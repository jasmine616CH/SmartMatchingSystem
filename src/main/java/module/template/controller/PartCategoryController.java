package module.template.controller;

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

@RequestMapping("/api/category")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartCategoryController {

    // 查询分类树
    @GetMapping("/tree")
    public Result<?> queryCategoryTree() {
        return Result.success();
    }

    //查询分类详情接口
    @GetMapping("/{catId}")
    public Result<?> queryCategoryDetail(@NotNull(message = "catId 不能为空") @PathVariable Long catId) {
        return Result.success();
    }

    // 新增分类接口
    @PostMapping("")
    public Result<?> addCategory(@Valid @RequestBody PartCategorySaveDTO partCategorySaveDTO) {
        return Result.success();
    }

    // 修改分类接口
    @PutMapping("/{catId}")
    public Result<?> updateCategory(@NotNull(message = "catId 不能为空") @PathVariable Long catId) {
        return Result.success();
    }

    // 删除分类接口
    @DeleteMapping("/{catId}")
    public Result<?> deleteCategory(@NotNull(message = "catId 不能为空") @PathVariable Long catId) {
        return Result.success();
    }


}
