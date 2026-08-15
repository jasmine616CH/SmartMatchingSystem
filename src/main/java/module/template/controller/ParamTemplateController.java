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
import module.template.dto.ParamTemplateSaveDTO;

@RequestMapping("/api/part/param")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class ParamTemplateController {
    

    //新建模板
    @PostMapping("template")
    public Result<?> addTemplate(@Valid @RequestBody ParamTemplateSaveDTO paramTemplateSaveDTO){
        return Result.success();
    }

    //修改模板
    @PutMapping("template/{templateId}")
    public Result<?> updateTemplate(@NotNull(message = "templateId 不能为空") @PathVariable Long templateId){
        return Result.success();
    }
                                                                                                                                                                                        
    //删除模板
    @DeleteMapping("template/{templateId}")
    public Result<?> deleteTemplate(@NotNull(message = "templateId 不能为空") @PathVariable Long templateId){
        return Result.success();
    }    
    

    //获取模板列表
    @GetMapping("template/{catId}")
    public Result<?> queryTemplateList(@NotNull(message = "catId 不能为空") @PathVariable Long catId){
        return Result.success();
    }

    //获取模板详情
    @GetMapping("template/{templateId}")
    public Result<?> queryTemplateDetail(@NotNull(message = "templateId 不能为空") @PathVariable Long templateId){
        return Result.success();
    }
}
