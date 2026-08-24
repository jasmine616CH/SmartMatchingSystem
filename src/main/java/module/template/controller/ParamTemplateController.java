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
import module.template.dto.ParamTemplateSaveDTO;
import module.template.dto.ParamTemplateUpdateDTO;
import module.template.service.ParamTemplateService;
import module.template.vo.ParamTemplateDetailVO;
import module.template.vo.ParamTemplateBriefVO;

/**
 * 参数模板管理控制器
 */
@RequestMapping("/api/param")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class ParamTemplateController {

    private final ParamTemplateService paramTemplateService;

    /**
     * 查询参数模板列表
     *
     * @param catId 分类ID
     * @return 参数模板列表
     */
    @GetMapping("/template/{catId}")
    public Result<ParamTemplateBriefVO> queryTemplateList(
            @NotNull(message = "catId 不能为空") @PathVariable Long catId) {
        return Result.success(paramTemplateService.queryTemplateList(catId));
    }

    /**
     * 查询参数模板详情
     *
     * @param templateId 参数模板ID
     * @return 参数模板详情
     */
    @GetMapping("/template/{templateId}")
    public Result<ParamTemplateDetailVO> queryTemplateDetail(
            @NotNull(message = "templateId 不能为空") @PathVariable Long templateId) {
        return Result.success(paramTemplateService.queryTemplateDetail(templateId));
    }

    /**
     * 新增模板
     * 
     * @param paramTemplateSaveDTO
     * @return
     */
    @PostMapping("/template")
    public Result<?> addTemplate(@Valid @RequestBody ParamTemplateSaveDTO paramTemplateSaveDTO) {
        paramTemplateService.addTemplate(paramTemplateSaveDTO);
        return Result.success();
    }

    /**
     * 修改模板
     * 
     * @param paramTemplateUpdateDTO
     * @return
     */
    @PutMapping("/template")
    public Result<?> updateTemplate(@Valid @RequestBody ParamTemplateUpdateDTO paramTemplateUpdateDTO) {
        paramTemplateService.updateTemplate(paramTemplateUpdateDTO);
        return Result.success();
    }

    /**
     * 删除模板
     * 
     * @param templateId
     * @return
     */
    @DeleteMapping("/template/{templateId}")
    public Result<?> deleteTemplate(@NotNull(message = "templateId 不能为空") @PathVariable Long templateId) {
        paramTemplateService.deleteTemplate(templateId);
        return Result.success();
    }

    /**
     * 提交审核
     * 
     * @param templateId
     * @return
     */
    @PostMapping("/submitAudit/{templateId}") // 提交审核：草稿→待审核
    public Result<?> submitAudit(@NotNull(message = "templateId 不能为空") @PathVariable Long templateId) {
        paramTemplateService.submitAudit(templateId);
        return Result.success();
    }

    /**
     * 撤回
     * 
     * @param templateId
     * @return
     */
    @PostMapping("/revoke/{templateId}") // 撤回：已发布→待审核
    public Result<?> revoke(@NotNull(message = "templateId 不能为空") @PathVariable Long templateId) {
        paramTemplateService.revoke(templateId);
        return Result.success();
    }
}
