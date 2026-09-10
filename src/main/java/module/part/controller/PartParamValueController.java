package module.part.controller;

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
import module.part.dto.PartParamValueSaveDTO;
import module.part.dto.PartParamValueUpdateDTO;
import module.part.dto.SaveParamDTO;
import module.part.service.PartParamValueService;
import module.part.vo.PartParamFieldVO;
import module.part.vo.PartParamValueVO;
import module.part.vo.SaveParamResultVO;
import module.system.annotation.OperateLog;

/**
 * 配件参数管理控制器
 */
@RequestMapping("/api/part/param/value")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartParamValueController {

    private final PartParamValueService partParamValueService;

    // 获取配件参数信息详情
    @GetMapping("/{paramValId}")
    public Result<PartParamValueVO> queryPartInfoParamValueDetail(
        @NotNull @PathVariable("paramValId") Long paramValId) {
        return Result.success();
    }

    
    /**
     * 查询配件模板参数列表（动态）
     *
     * @param partId
     * @return
     */
    @GetMapping("/{partId}/fields")
    public Result<List<PartParamFieldVO>> listFields(
        @NotNull @PathVariable("partId") Long partId) {
        return Result.success(partParamValueService.listFieldVO(partId));
    }

    /**
     * 单条参数保存：有值即新增/覆盖，清空即删除，返回最新列表 + 被自动清除的脏数据
     */
    @OperateLog (
        operateDesc = "保存配件参数值",
        operateType = OperateType.UPDATE,
        operateModule = OperateModule.PART
    )
    
    @PostMapping("/{partId}/save")
    public Result<SaveParamResultVO> saveParamValue(
            @NotNull @PathVariable("partId") Long partId,
            @Valid @RequestBody SaveParamDTO saveParamDTO) {
        return Result.success(partParamValueService.saveSingleParam(partId, saveParamDTO));
    }

    /**
     * 整套提交校验：全局必填 + 条件必填双向校验，不通过返回全部错误信息
     */
    @PostMapping("/{partId}/submit")
    public Result<Void> submitParamValue(
            @NotNull @PathVariable("partId") Long partId) {
        partParamValueService.submitAll(partId);
        return Result.success();
    }

    // 新增配件参数信息
    @OperateLog (
        operateDesc = "新增配件参数信息",
        operateType = OperateType.ADD,
        operateModule = OperateModule.PART
    )
    @PostMapping("")
    public Result<?> addPartInfoParamValue(
            @Valid @RequestBody PartParamValueSaveDTO partParamValueSaveDTO) {
        partParamValueService.addPartParamValue(partParamValueSaveDTO);
        return Result.success();
    }

    // 修改配件参数信息
    @OperateLog (
        operateDesc = "修改配件参数信息",
        operateType = OperateType.UPDATE,
        operateModule = OperateModule.PART
    )
    @PutMapping("/{partId}")
    public Result<?> updatePartInfoParamValue(
            @Valid @RequestBody PartParamValueUpdateDTO partParamValueUpdateDTO) {
        partParamValueService.updatePartParamValue(partParamValueUpdateDTO);
        return Result.success();
    }

    // 删除配件参数信息
    @OperateLog (
        operateDesc = "删除配件参数信息",
        operateType = OperateType.DELETE,
        operateModule = OperateModule.PART
    )
    @DeleteMapping("/{paramValId}")
    public Result<?> deletePartInfoParamValue(
        @NotNull @PathVariable("paramValId") Long paramValId) {
        partParamValueService.deletePartParamValue(paramValId);
        return Result.success();
    }

}
