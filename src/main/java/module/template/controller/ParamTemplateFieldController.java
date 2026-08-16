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
import module.template.dto.ParamTemplateFieldDTO;
import module.template.service.ParamTemplateFieldService;

@RequestMapping("/api/param/template")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class ParamTemplateFieldController {

    private final ParamTemplateFieldService paramTemplateFieldService;

    // 获取模板参数清单
    @GetMapping("/{templateId}")
    public Result<?> queryTemplateFieldList(
            @NotNull(message = "templateId 不能为空") @PathVariable Long templateId) {
        return Result.success();
    }

    // 新增模板参数
    @PostMapping("/{templateId}/field")
    public Result<?> addTemplateField(
            @Valid @RequestBody ParamTemplateFieldDTO paramTemplateFieldDTO) {
        return Result.success();
    }

    // 修改模板参数
    @PutMapping("/{templateId}/field/{fieldId}")
    public Result<?> updateTemplateField(
            @NotNull(message = "templateId 不能为空") @PathVariable Long templateId,
            @NotNull(message = "fieldId 不能为空") @PathVariable Long fieldId) {
        return Result.success();
    }

    // 删除模板参数
    @DeleteMapping("/{templateId}/field/{fieldId}")
    public Result<?> deleteTemplateField(
            @NotNull(message = "templateId 不能为空") @PathVariable Long templateId,
            @NotNull(message = "fieldId 不能为空") @PathVariable Long fieldId) {
        return Result.success();
    }
}
