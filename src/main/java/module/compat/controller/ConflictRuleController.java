package module.compat.controller;

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
import java.util.List;

import module.compat.dto.ConflictRuleQueryDTO;
import module.compat.dto.ConflictRuleSaveDTO;
import module.compat.dto.ConflictRuleUpdateDTO;
import module.compat.dto.ExprValidateDTO;
import module.compat.service.ConflictRuleService;
import module.compat.vo.ConflictRuleVO;
import module.compat.vo.ExprValidateVO;
import module.compat.vo.ParamCodeOptionVO;
import module.compat.vo.SubsystemVO;
import module.system.annotation.OperateLog;

/**
 * 配件兼容冲突规则管理控制器
 * <p>
 * 规则定义「两个配件之间什么算冲突」，是跨配件兼容性检测的前提。
 */
@RequestMapping("/api/compat/rule")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class ConflictRuleController {

    private final ConflictRuleService conflictRuleService;

    // ==================== 规则编辑器的辅助接口 ====================

    /**
     * 查询二级子系统候选（规则的两侧下拉）
     *
     * @return 二级子系统列表
     */
    @GetMapping("/subsystems")
    public Result<List<SubsystemVO>> querySubsystems() {
        return Result.success(conflictRuleService.querySubsystems());
    }

    /**
     * 查询某二级子系统下的参数编码候选（表达式自动补全用）
     *
     * @param catId 二级子系统ID
     * @return 参数编码候选
     */
    @GetMapping("/param-codes")
    public Result<List<ParamCodeOptionVO>> queryParamCodes(
            @NotNull(message = "catId不能为空") @RequestParam("catId") Long catId) {
        return Result.success(conflictRuleService.queryParamCodes(catId));
    }

    /**
     * 校验表达式语法（供编辑器边输入边校验，不落库）
     *
     * @param dto 表达式
     * @return 校验结果
     */
    @PostMapping("/validate-expr")
    public Result<ExprValidateVO> validateExpr(
            @Valid @RequestBody ExprValidateDTO dto) {
        return Result.success(conflictRuleService.validateExpr(dto.getCheckExpr()));
    }

    /**
     * 分页查询冲突规则
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Page<ConflictRuleVO>> queryRuleList(
            @Valid ConflictRuleQueryDTO dto) {
        return Result.success(conflictRuleService.queryRuleList(dto));
    }

    /**
     * 查询冲突规则详情
     *
     * @param ruleId 规则主键ID
     * @return 规则详情
     */
    @GetMapping("/{ruleId}")
    public Result<ConflictRuleVO> queryRuleDetail(
            @NotNull(message = "ruleId不能为空") @PathVariable("ruleId") Long ruleId) {
        return Result.success(conflictRuleService.queryRuleDetail(ruleId));
    }

    /**
     * 新增冲突规则
     *
     * @param dto 规则信息
     * @return 新增结果
     */
    @OperateLog(operateDesc = "新增兼容冲突规则", operateType = OperateType.ADD, operateModule = OperateModule.COMPATIBLE_RULE)
    @PostMapping("")
    public Result<?> addRule(
            @Valid @RequestBody ConflictRuleSaveDTO dto) {
        conflictRuleService.addRule(dto);
        return Result.success();
    }

    /**
     * 修改冲突规则
     *
     * @param dto 规则信息
     * @return 修改结果
     */
    @OperateLog(operateDesc = "修改兼容冲突规则", operateType = OperateType.UPDATE, operateModule = OperateModule.COMPATIBLE_RULE)
    @PutMapping("")
    public Result<?> updateRule(
            @Valid @RequestBody ConflictRuleUpdateDTO dto) {
        conflictRuleService.updateRule(dto);
        return Result.success();
    }

    /**
     * 删除冲突规则
     *
     * @param ruleId 规则主键ID
     * @return 删除结果
     */
    @OperateLog(operateDesc = "删除兼容冲突规则", operateType = OperateType.DELETE, operateModule = OperateModule.COMPATIBLE_RULE)
    @DeleteMapping("/{ruleId}")
    public Result<?> deleteRule(
            @NotNull(message = "ruleId不能为空") @PathVariable("ruleId") Long ruleId) {
        conflictRuleService.deleteRule(ruleId);
        return Result.success();
    }
}
