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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.system.vo.DictOptionVO;
import module.template.dto.ParamTemplateFieldSaveDTO;
import module.template.dto.ParamTemplateFieldUpdateDTO;
import module.template.service.ParamTemplateFieldService;
import module.template.service.UnitService;
import module.template.vo.CurrentTemplateFieldListVO;
import module.template.vo.ParamTemplateFieldListVO;
import module.template.vo.ParamTemplateFieldVO;
import module.template.vo.UnitBaseVO;

/**
 * 参数模板控制器
 */
@RequestMapping("/api/param/template")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class ParamTemplateFieldController {

    private final ParamTemplateFieldService paramTemplateFieldService;
    private final UnitService unitService;

    /**
     * 查询参数模板字段列表
     *
     * @param templateId
     * @return
     */
    @GetMapping("/{templateId}")
    public Result<List<ParamTemplateFieldListVO>> queryTemplateFieldList(
            @NotNull(message = "templateId 不能为空") @PathVariable Long templateId) {
        return Result.success(paramTemplateFieldService.queryTemplateFieldList(templateId));
    }

    /**
     * 查询参数模板字段详情
     * 
     * @param paramTemplateFieldVO
     * @return
     */
    @GetMapping("/field/{fieldId}")
    public Result<ParamTemplateFieldVO> queryTemplateFieldDetail(
            @NotNull(message = "fieldId 不能为空") @PathVariable Long fieldId) {
        return Result.success(paramTemplateFieldService.queryTemplateFieldDetail(fieldId));
    }

    /**
     * 查询当前模板参数列表
     * 
     * @param
     * @return
     */
    @GetMapping("/field/{templateId}")
    public Result<List<CurrentTemplateFieldListVO>> queryCurrentTemplateFieldList(
            @NotNull(message = "templateId 不能为空") @PathVariable Long templateId) {
        return Result.success(paramTemplateFieldService.queryCurrentTemplateFieldList(templateId));
    }

    /**
     * 获取枚举类型参数的枚举值
     * 
     * @param
     * @return
     */
    @GetMapping("/field/enum/{fieldId}")
    public Result<List<DictOptionVO>> queryEnumParamValues(
            @NotNull(message = "fieldId 不能为空") @PathVariable Long fieldId) {
        return Result.success(paramTemplateFieldService.queryEnumParamValues(fieldId));
    }

    /**
     * 获取所有根基准单位（用于“数据库基准单位”下拉）
     */
    @GetMapping("/unit/base-list")
    public Result<List<UnitBaseVO>> getBaseUnitList() {
        return Result.success(unitService.getBaseUnitList());
    }

    /**
     * 根据基准单位编码，获取该基准下的所有单位（用于“页面默认展示单位”下拉）
     */
    @GetMapping("/unit/list-by-base")
    public Result<List<UnitBaseVO>> getUnitListByBase(
            @RequestParam @NotNull String unitCode) {
        return Result.success(unitService.getUnitListByBase(unitCode));
    }

    /**
     * 新增模板参数
     * 
     * @param paramTemplateFieldDTO
     * @return
     */
    @PostMapping("/{templateId}/field")
    public Result<?> addTemplateField(
            @Valid @RequestBody ParamTemplateFieldSaveDTO paramTemplateFieldDTO) {
        paramTemplateFieldService.addTemplateField(paramTemplateFieldDTO);
        return Result.success();
    }


    /**
     * 修改模板参数
     * 
     * @param paramTemplateFieldUpdateDTO
     * @return
     */
    @PutMapping("/{templateId}/field/{fieldId}")
    public Result<?> updateTemplateField(
            @Valid @RequestBody ParamTemplateFieldUpdateDTO paramTemplateFieldUpdateDTO) {
        paramTemplateFieldService.updateTemplateField(paramTemplateFieldUpdateDTO);
        return Result.success();
    }

    /**
     * 删除模板参数
     * 
     * @param fieldId
     * @return
     */
    @DeleteMapping("/field/{fieldId}")
    public Result<?> deleteTemplateField(
            @NotNull(message = "fieldId 不能为空") @PathVariable Long fieldId) {
        paramTemplateFieldService.deleteTemplateField(fieldId);
        return Result.success();
    }
}
