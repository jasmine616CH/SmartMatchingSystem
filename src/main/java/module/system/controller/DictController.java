package module.system.controller;

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

import common.enums.OperateModule;
import common.enums.OperateType;
import common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.system.annotation.OperateLog;
import module.system.dto.DictItemSaveDTO;
import module.system.dto.DictItemUpdateDTO;
import module.system.dto.DictTypeQueryDTO;
import module.system.dto.DictTypeSaveDTO;
import module.system.dto.DictTypeUpdateDTO;
import module.system.service.SysDictItemService;
import module.system.service.SysDictTypeService;
import module.system.vo.DictDetailVO;
import module.system.vo.DictItemVO;
import module.system.vo.DictTypeVO;

/**
 * 数据字典管理控制器
 * <p>
 * 字典类型与字典项两级：类型是「一张下拉表」，项是表里的选项。
 */
@RequestMapping("/api/dict")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class DictController {

    private final SysDictTypeService sysDictTypeService;

    private final SysDictItemService sysDictItemService;

    // ==================== 字典类型 ====================

    /**
     * 分页查询字典类型
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/type/list")
    public Result<Page<DictTypeVO>> queryDictTypeList(
            @Valid DictTypeQueryDTO dto) {
        return Result.success(sysDictTypeService.queryDictTypePage(dto));
    }

    /**
     * 查询字典类型详情
     *
     * @param dictTypeId 字典类型主键ID
     * @return 字典类型
     */
    @GetMapping("/type/{dictTypeId}")
    public Result<DictTypeVO> queryDictTypeDetail(
            @NotNull(message = "dictTypeId不能为空") @PathVariable("dictTypeId") Long dictTypeId) {
        return Result.success(sysDictTypeService.queryDictTypeDetail(dictTypeId));
    }

    /**
     * 新增字典类型
     *
     * @param dto 字典类型信息
     * @return 新增结果
     */
    @OperateLog(operateDesc = "新增字典类型", operateType = OperateType.ADD, operateModule = OperateModule.DICT)
    @PostMapping("/type")
    public Result<?> addDictType(
            @Valid @RequestBody DictTypeSaveDTO dto) {
        sysDictTypeService.addDictType(dto);
        return Result.success();
    }

    /**
     * 修改字典类型
     *
     * @param dto 字典类型信息
     * @return 修改结果
     */
    @OperateLog(operateDesc = "修改字典类型", operateType = OperateType.UPDATE, operateModule = OperateModule.DICT)
    @PutMapping("/type")
    public Result<?> updateDictType(
            @Valid @RequestBody DictTypeUpdateDTO dto) {
        sysDictTypeService.updateDictType(dto);
        return Result.success();
    }

    /**
     * 删除字典类型
     *
     * @param dictTypeId 字典类型主键ID
     * @return 删除结果
     */
    @OperateLog(operateDesc = "删除字典类型", operateType = OperateType.DELETE, operateModule = OperateModule.DICT)
    @DeleteMapping("/type/{dictTypeId}")
    public Result<?> deleteDictType(
            @NotNull(message = "dictTypeId不能为空") @PathVariable("dictTypeId") Long dictTypeId) {
        sysDictTypeService.deleteDictType(dictTypeId);
        return Result.success();
    }

    // ==================== 对外取值接口 ====================

    /**
     * 按字典编码取启用的字典项（通用下拉数据源）
     * <p>
     * 只返回 status=1 的项并按 sort 升序，供业务下拉直接使用；
     * 管理页面要连停用项一起看，请用 {@code GET /api/dict/item/list}。
     *
     * @param dictCode 字典唯一编码
     * @return 字典内容（含 dictName 与选项列表）
     */
    @GetMapping("/{dictCode}/items")
    public Result<DictDetailVO> getDictItems(
            @NotNull(message = "dictCode不能为空") @PathVariable("dictCode") String dictCode) {
        return Result.success(sysDictTypeService.getEnabledDictDetail(dictCode));
    }

    // ==================== 字典项 ====================

    /**
     * 查询某字典下的全部字典项
     * <p>管理页面需要看到停用项，故不按状态过滤。
     *
     * @param dictTypeId 字典类型主键ID
     * @return 字典项列表
     */
    @GetMapping("/item/list")
    public Result<List<DictItemVO>> queryDictItemList(
            @NotNull(message = "dictTypeId不能为空") @RequestParam("dictTypeId") Long dictTypeId) {
        return Result.success(sysDictItemService.queryItemList(dictTypeId));
    }

    /**
     * 新增字典项
     *
     * @param dto 字典项信息
     * @return 新增结果
     */
    @OperateLog(operateDesc = "新增字典项", operateType = OperateType.ADD, operateModule = OperateModule.DICT)
    @PostMapping("/item")
    public Result<?> addDictItem(
            @Valid @RequestBody DictItemSaveDTO dto) {
        sysDictItemService.addItem(dto);
        return Result.success();
    }

    /**
     * 修改字典项
     *
     * @param dto 字典项信息
     * @return 修改结果
     */
    @OperateLog(operateDesc = "修改字典项", operateType = OperateType.UPDATE, operateModule = OperateModule.DICT)
    @PutMapping("/item")
    public Result<?> updateDictItem(
            @Valid @RequestBody DictItemUpdateDTO dto) {
        sysDictItemService.updateItem(dto);
        return Result.success();
    }

    /**
     * 删除字典项
     *
     * @param dictItemId 字典项主键ID
     * @return 删除结果
     */
    @OperateLog(operateDesc = "删除字典项", operateType = OperateType.DELETE, operateModule = OperateModule.DICT)
    @DeleteMapping("/item/{dictItemId}")
    public Result<?> deleteDictItem(
            @NotNull(message = "dictItemId不能为空") @PathVariable("dictItemId") Long dictItemId) {
        sysDictItemService.deleteItem(dictItemId);
        return Result.success();
    }
}
