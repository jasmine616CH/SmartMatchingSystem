package module.part.controller;

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
import module.part.service.PartParamValueService;
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


    //TODO
    // 获取配件参数信息详情
    @GetMapping("/{partId}")
    public Result<?> queryPartInfoParamValueDetail() {
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
