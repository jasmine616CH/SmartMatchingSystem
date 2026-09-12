package module.scheme.controller;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.scheme.service.PartCategoryService;
import module.scheme.vo.PartCategoryTreeVO;
import module.scheme.vo.PartParameterValueVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *配件选配中心功能模块控制器
 * 实现配件选配功能
 */
@RequestMapping("/api/category")
@RequiredArgsConstructor
@RestController
@Slf4j
public class PartCategoryController {

    @Autowired
    private PartCategoryService partCategoryService;

    /**
     * 返回配件三级分类表
     * @return 返回三级分类表列表
     */
    @GetMapping("/get-tree")
    public Result<List<PartCategoryTreeVO>> getPartCategoryTree(){
        return Result.success(partCategoryService.getPartCategoryTree());
    }

    /**
     * 查看配件简略信息
     * @param catId 配件id
     * @return 返回配件简略信息
     */
    @GetMapping("/get-value")
    public Result<List<List<PartParameterValueVO>>> getPartParameterValue(Long catId){
        return Result.success(partCategoryService.getPartParameterValue(catId));
    }

}
