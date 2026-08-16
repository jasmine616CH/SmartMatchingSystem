package module.part.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.part.dto.PartInfoDTO;

@RequestMapping("/api/part")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartInfoController {

    // 获取配件信息列表（条件查询）
    @GetMapping("/list")
    public Result<?> queryPartInfoList() {
        return Result.success();
    }

    // 新增配件信息
    @PostMapping("")
    public Result<?> addPartInfo(@Valid @RequestBody PartInfoDTO partInfoDTO) {
        return Result.success();
    }

    //修改配件信息
    @PutMapping("/{partId}")
    public Result<?> updatePartInfo() {
        return Result.success();
    }

    // 删除配件信息
    @DeleteMapping("/{partId}")
    public Result<?> deletePartInfo() {
        return Result.success();
    }

    
}
