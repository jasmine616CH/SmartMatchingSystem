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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.part.dto.PartInfoSaveDTO;
import module.part.dto.PartInfoUpdateDTO;
import module.part.service.PartInfoService;
import module.part.vo.PartInfoListVO;

/**
 * 配件信息管理控制器
 */
@RequestMapping("/api/part")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartInfoController {

    private final PartInfoService partInfoService;

    //TODO 条件查询
    /**
     * 获取配件信息列表
     * 
     * @param catId
     * @param pageNum
     * @param pageSize
     * @return
     * @author 徐宝福
     */
    @GetMapping("/{catId}/list")
    public Result<Page<PartInfoListVO>> queryPartInfoList(
        @NotNull(message = "catId不能为空") @PathVariable("catId") Long catId,
        @NotNull(message = "pageNum不能为空") @RequestParam(defaultValue = "1") Integer pageNum,
        @NotNull(message = "pageSize不能为空") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(partInfoService.queryPartInfoList(catId, pageNum, pageSize));
    }

    /**
     * 新增配件信息
     * 
     * @param partInfoDTO
     * @return
     * @author 徐宝福
     */
    @PostMapping("")
    public Result<?> addPartInfo(
        @Valid @RequestBody PartInfoSaveDTO partInfoSvaeDTO) {
        partInfoService.addPartInfo(partInfoSvaeDTO);
        return Result.success();
    }

    /**
     * 修改配件信息
     * 
     * @param partId
     * @param partInfoDTO
     * @return
     * @author 徐宝福
     */
    @PutMapping("/{partId}")
    public Result<?> updatePartInfo(
        @Valid @RequestBody PartInfoUpdateDTO partInfoUpdateDTO
    ) {
        return Result.success();
    }

    /**
     * 删除配件信息
     * 
     * @param partId
     * @return
     * @author 徐宝福
     */
    @DeleteMapping("/{partId}")
    public Result<?> deletePartInfo() {
        return Result.success();
    }

}
