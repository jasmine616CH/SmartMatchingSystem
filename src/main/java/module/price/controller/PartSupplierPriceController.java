package module.price.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import common.enums.OperateModule;
import common.enums.OperateType;
import common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.price.dto.PartSupplierPriceQueryDTO;
import module.price.dto.PartSupplierPriceSaveDTO;
import module.price.dto.PartSupplierPriceUpdateDTO;
import module.price.service.PartSupplierPriceService;
import module.price.vo.PartSupplierPriceDetailVO;
import module.price.vo.PartSupplierPriceListVO;
import module.system.annotation.OperateLog;

/**
 * 配件供应商报价管理控制器
 * 实现报价的增删改查
 */
@RequestMapping("/api/price")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class PartSupplierPriceController {

    private final PartSupplierPriceService partSupplierPriceService;

    /**
     * 分页查询报价列表
     * <p>
     * 返回行带齐配件、分类、供应商与报价各字段，支持表格列筛选；
     * 查询条件均为可选，不传即不过滤。
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Page<PartSupplierPriceListVO>> queryPriceList(
            @Valid PartSupplierPriceQueryDTO dto) {
        return Result.success(partSupplierPriceService.queryPriceList(dto));
    }

    /**
     * 查询报价详情
     *
     * @param priceId 报价主键ID
     * @return 报价详情
     */
    @GetMapping("/{priceId}")
    public Result<PartSupplierPriceDetailVO> queryPriceDetail(
            @NotNull(message = "priceId不能为空") @PathVariable("priceId") Long priceId) {
        return Result.success(partSupplierPriceService.queryPriceDetail(priceId));
    }

    /**
     * 新增报价
     *
     * @param dto 报价信息
     * @return 新增结果
     */
    @OperateLog(operateDesc = "新增配件供应商报价", operateType = OperateType.ADD, operateModule = OperateModule.PRICE)
    @PostMapping("")
    public Result<?> addPrice(
            @Valid @RequestBody PartSupplierPriceSaveDTO dto) {
        partSupplierPriceService.addPrice(dto);
        return Result.success();
    }

    /**
     * 修改报价
     *
     * @param dto 报价信息
     * @return 修改结果
     */
    @OperateLog(operateDesc = "修改配件供应商报价", operateType = OperateType.UPDATE, operateModule = OperateModule.PRICE)
    @PutMapping("")
    public Result<?> updatePrice(
            @Valid @RequestBody PartSupplierPriceUpdateDTO dto) {
        partSupplierPriceService.updatePrice(dto);
        return Result.success();
    }

    /**
     * 删除报价
     *
     * @param priceId 报价主键ID
     * @return 删除结果
     */
    @OperateLog(operateDesc = "删除配件供应商报价", operateType = OperateType.DELETE, operateModule = OperateModule.PRICE)
    @DeleteMapping("/{priceId}")
    public Result<?> deletePrice(
            @NotNull(message = "priceId不能为空") @PathVariable("priceId") Long priceId) {
        partSupplierPriceService.deletePrice(priceId);
        return Result.success();
    }
}
