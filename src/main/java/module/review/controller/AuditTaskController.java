package module.review.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import module.review.dto.AuditApproveDTO;
import module.review.dto.AuditRejectDTO;
import module.review.dto.AuditTaskQueryDTO;
import module.review.service.AuditTaskService;
import module.review.vo.AuditDetailVO;
import module.review.vo.AuditRecordVO;
import module.system.annotation.OperateLog;

/**
 * 通用审批中心控制器
 * <p>
 * 审批接口只认 auditId（审批任务身份），不接收 bizType / bizId，
 * 因此新增可审批业务时本控制器无需改动。
 */
@RequestMapping("/api/review")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class AuditTaskController {

    private final AuditTaskService auditTaskService;

    /**
     * 分页查询审批任务
     * <p>
     * 待办列表 = GET /api/review/list?status=0，可再叠加 bizType 过滤。
     * status 不传即不过滤。
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<Page<AuditRecordVO>> queryAuditList(
            @Valid AuditTaskQueryDTO dto) {
        return Result.success(auditTaskService.queryAuditPage(dto));
    }

    /**
     * 查询审批详情（审批任务信息 + 业务明细）
     *
     * @param auditId 审批任务主键ID
     * @return 审批详情
     */
    @GetMapping("/detail/{auditId}")
    public Result<AuditDetailVO> queryAuditDetail(
            @NotNull(message = "auditId不能为空") @PathVariable("auditId") Long auditId) {
        return Result.success(auditTaskService.queryAuditDetail(auditId));
    }

    /**
     * 查询某业务对象的完整审批历史
     *
     * @param bizType 业务类型编码，如 SUPPLIER
     * @param bizId   业务对象主键
     * @return 审批记录列表
     */
    @GetMapping("/history/{bizType}/{bizId}")
    public Result<List<AuditRecordVO>> queryHistory(
            @NotNull(message = "bizType不能为空") @PathVariable("bizType") String bizType,
            @NotNull(message = "bizId不能为空") @PathVariable("bizId") Long bizId) {
        return Result.success(auditTaskService.queryHistory(bizType, bizId));
    }

    /**
     * 审批通过
     *
     * @param dto 审批入参
     * @return 审批结果
     */
    @OperateLog(operateDesc = "审批通过", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/approve")
    public Result<?> approve(
            @Valid @RequestBody AuditApproveDTO dto) {
        auditTaskService.approve(dto);
        return Result.success();
    }

    /**
     * 审批驳回
     *
     * @param dto 审批入参
     * @return 审批结果
     */
    @OperateLog(operateDesc = "审批驳回", operateType = OperateType.AUDIT, operateModule = OperateModule.AUDIT)
    @PostMapping("/reject")
    public Result<?> reject(
            @Valid @RequestBody AuditRejectDTO dto) {
        auditTaskService.reject(dto);
        return Result.success();
    }
}
