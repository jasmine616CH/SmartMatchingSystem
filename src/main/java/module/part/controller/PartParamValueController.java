package module.part.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 配件参数管理控制器
 */
@RequestMapping("/api/part/param/value")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartParamValueController {

    // 获取配件参数信息详情
    @GetMapping("/{partId}")
    public Result<?> queryPartInfoDetail() {
        return Result.success();
    }

    // 新增配件参数信息
    @PostMapping("")
    public Result<?> addPartInfoParam() {
        return Result.success();
    }

    // 修改配件参数信息
    @PutMapping("/{partId}")
    public Result<?> updatePartInfoParam() {
        return Result.success();
    }

    // 删除配件参数信息
    @DeleteMapping("/{partId}")
    public Result<?> deletePartInfoParam() {
        return Result.success();
    }

}
