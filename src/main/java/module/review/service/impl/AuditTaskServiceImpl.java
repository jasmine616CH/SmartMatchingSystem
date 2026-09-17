package module.review.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import common.enums.AuditStatus;
import common.enums.BizType;
import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.SecurityUtils;
import lombok.RequiredArgsConstructor;
import module.review.dto.AuditApproveDTO;
import module.review.dto.AuditRejectDTO;
import module.review.dto.AuditTaskQueryDTO;
import module.review.entity.AuditRecord;
import module.review.mapper.AuditRecordMapper;
import module.review.service.AuditTaskService;
import module.review.spi.BizAuditHandler;
import module.review.spi.BizAuditHandlerRegistry;
import module.review.vo.AuditBizSummaryVO;
import module.review.vo.AuditDetailVO;
import module.review.vo.AuditRecordVO;

/**
 * 通用审批中心业务实现类
 * <p>
 * 本类是整个审批流唯一开启事务的地方，业务表的 CAS 与 audit_record 的写入
 * 必须在同一事务内，任一失败即整体回滚。
 */
@RequiredArgsConstructor
@Service
public class AuditTaskServiceImpl implements AuditTaskService {

    private final AuditRecordMapper auditRecordMapper;

    private final BizAuditHandlerRegistry handlerRegistry;

    // ==================== 业务模块调用 ====================

    /**
     * 提交审核：新建一条待审核任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPendingTask(BizType bizType, Long bizId) {
        if (bizType == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "bizType 不能为空");
        }
        if (bizId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "bizId 不能为空");
        }
        // 同一业务至多一条待审核任务：schema 上没加唯一键（那会挡住驳回后重新提交），
        // 靠业务表的状态 CAS 配合本校验共同保证
        if (countPending(bizType, bizId) > 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该数据已存在待审核任务，请勿重复提交");
        }

        AuditRecord record = new AuditRecord();
        record.setBizType(bizType.getCode());
        record.setBizId(bizId);
        record.setStatus(AuditStatus.PENDING.getCode());
        record.setSubmitUserId(SecurityUtils.getCurrentUserId());
        auditRecordMapper.insert(record);
    }

    /**
     * 撤回：把最近一条「已通过」的审批记录标记为「已撤回」
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeLatestApproved(BizType bizType, Long bizId) {
        AuditRecord latest = auditRecordMapper.selectLatestByBiz(bizType.getCode(), bizId);
        if (latest == null) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "未找到可撤回的审批记录");
        }
        if (!AuditStatus.APPROVED.getCode().equals(latest.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "未找到可撤回的审批记录");
        }
        // 只标记不删除：审批痕迹正是审计需要的东西，删掉的行也无法与「从未存在」区分
        markStatus(latest.getAuditId(), AuditStatus.APPROVED, AuditStatus.REVOKED, null);
    }

    /**
     * 撤销申请：把待审核的任务标记为「已撤回」
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeLatestPending(BizType bizType, Long bizId) {
        AuditRecord latest = auditRecordMapper.selectLatestByBiz(bizType.getCode(), bizId);
        if (latest == null || !AuditStatus.PENDING.getCode().equals(latest.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "未找到可撤销的审批申请");
        }
        markStatus(latest.getAuditId(), AuditStatus.PENDING, AuditStatus.REVOKED, null);
    }

    /**
     * 统计某业务对象当前待审核的任务条数
     */
    @Override
    public long countPending(BizType bizType, Long bizId) {
        if (bizType == null || bizId == null) {
            return 0L;
        }
        return auditRecordMapper.countPending(bizType.getCode(), bizId);
    }

    // ==================== 审批中心查询与审批 ====================

    /**
     * 分页查询审批任务
     */
    @Override
    public Page<AuditRecordVO> queryAuditPage(AuditTaskQueryDTO dto) {
        AuditTaskQueryDTO query = dto == null ? new AuditTaskQueryDTO() : dto;

        Page<AuditRecordVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<AuditRecordVO> result = auditRecordMapper.selectAuditPage(page, query);
        fillBizSummary(result.getRecords());
        return result;
    }

    /**
     * 审批详情：审批任务信息 + 业务明细
     */
    @Override
    public AuditDetailVO queryAuditDetail(Long auditId) {
        if (auditId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "auditId 不能为空");
        }
        AuditRecord record = auditRecordMapper.selectById(auditId);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "审批任务不存在");
        }

        AuditRecordVO vo = toVO(record);
        BizAuditHandler handler = handlerRegistry.find(BizType.getByCode(record.getBizType()));
        Object bizDetail = null;
        if (handler != null) {
            AuditBizSummaryVO summary = handler.loadSummary(record.getBizId());
            applySummary(vo, summary);
            bizDetail = handler.loadDetail(record.getBizId());
        }

        AuditDetailVO detail = new AuditDetailVO();
        detail.setAudit(vo);
        detail.setBizDetail(bizDetail);
        return detail;
    }

    /**
     * 查询某业务对象的完整审批历史
     */
    @Override
    public List<AuditRecordVO> queryHistory(String bizType, Long bizId) {
        if (bizId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "bizId 不能为空");
        }
        BizType type = BizType.getByCode(bizType);
        if (type == null) {
            throw new BusinessException(ResultCode.PARAM_VALUE_INVALID, "不支持的业务类型：" + bizType);
        }
        List<AuditRecordVO> list = auditRecordMapper.selectHistory(type.getCode(), bizId);
        fillBizSummary(list);
        return list;
    }

    /**
     * 审批通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(AuditApproveDTO dto) {
        if (dto == null || dto.getAuditId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "auditId 不能为空");
        }
        // TODO 审批人角色校验：本仓库全局尚未启用方法级鉴权，
        // 且 sys_role 无种子数据时会导致流程无法联调，故暂不拦截。
        // 后续在此加 SecurityUtils.isStudent()（该方法实际校验的是 ROLE_APPROVER，名称有误导）。

        AuditRecord record = auditRecordMapper.selectById(dto.getAuditId());
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "审批任务不存在");
        }
        if (!AuditStatus.PENDING.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "该审批任务已处理，请刷新后重试");
        }

        BizAuditHandler handler = handlerRegistry.get(BizType.getByCode(record.getBizType()));
        Long auditUserId = SecurityUtils.getCurrentUserId();

        // 业务表状态机 CAS —— 真正的串行化点。
        // 两个审批人并发时，InnoDB 行锁使后者看到的状态已变更，更新 0 行，从而整体回滚。
        int bizRows = handler.markApproved(record.getBizId(), auditUserId);
        if (bizRows == 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "业务数据状态已变更，审批失败，请刷新后重试");
        }

        // 审批任务本身再做一次 CAS 兜底
        markStatus(dto.getAuditId(), AuditStatus.PENDING, AuditStatus.APPROVED, auditUserId);
    }

    /**
     * 审批驳回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(AuditRejectDTO dto) {
        if (dto == null || dto.getAuditId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "auditId 不能为空");
        }
        AuditRecord record = auditRecordMapper.selectById(dto.getAuditId());
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "审批任务不存在");
        }
        if (!AuditStatus.PENDING.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "该审批任务已处理，请刷新后重试");
        }

        BizAuditHandler handler = handlerRegistry.get(BizType.getByCode(record.getBizType()));
        Long auditUserId = SecurityUtils.getCurrentUserId();

        int bizRows = handler.markRejected(record.getBizId(), auditUserId);
        if (bizRows == 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "业务数据状态已变更，驳回失败，请刷新后重试");
        }

        markStatus(dto.getAuditId(), AuditStatus.PENDING, AuditStatus.REJECTED, auditUserId);
        // 驳回理由单独写：上面那条 CAS 已保证只有一个赢家，此处无需再加状态条件
        LambdaUpdateWrapper<AuditRecord> reasonWrapper = new LambdaUpdateWrapper<>();
        reasonWrapper.eq(AuditRecord::getAuditId, dto.getAuditId())
                .set(AuditRecord::getRejectReason, dto.getRejectReason());
        auditRecordMapper.update(null, reasonWrapper);
    }

    // ==================== 私有方法 ====================

    /**
     * 带原状态条件地更新审批任务（CAS），并把审批人与审批时间一并落库
     *
     * @param auditId       审批任务主键ID
     * @param expected      期望的原状态
     * @param target        目标状态
     * @param auditUserId   审批人用户ID，撤回场景传 null 表示保持原值不写
     */
    private void markStatus(Long auditId, AuditStatus expected, AuditStatus target, Long auditUserId) {
        LambdaUpdateWrapper<AuditRecord> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AuditRecord::getAuditId, auditId)
                .eq(AuditRecord::getStatus, expected.getCode())
                .set(AuditRecord::getStatus, target.getCode());
        if (auditUserId != null) {
            wrapper.set(AuditRecord::getAuditUserId, auditUserId)
                    .set(AuditRecord::getAuditTime, LocalDateTime.now());
        }
        // 实体参数必须传 null：否则 MP 会把实体的非空字段（含主键）一并拼进 SET 子句
        int rows = auditRecordMapper.update(null, wrapper);
        if (rows == 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "该审批任务已处理，请刷新后重试");
        }
    }

    /**
     * 批量回填业务摘要（bizName / bizCode / bizStatus）
     * <p>同一业务类型只调用一次 Handler 的批量方法，避免列表页 N+1。
     */
    private void fillBizSummary(List<AuditRecordVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Map<String, List<AuditRecordVO>> byType = records.stream()
                .filter(r -> r.getBizType() != null)
                .collect(Collectors.groupingBy(AuditRecordVO::getBizType));

        for (Map.Entry<String, List<AuditRecordVO>> entry : byType.entrySet()) {
            BizType type = BizType.getByCode(entry.getKey());
            // 宽松查找：未注册的类型不抛错，降级展示，避免历史脏数据把列表打成 500
            BizAuditHandler handler = type == null ? null : handlerRegistry.find(type);
            if (handler == null) {
                for (AuditRecordVO vo : entry.getValue()) {
                    vo.setBizDeleted(true);
                }
                continue;
            }
            Collection<Long> bizIds = entry.getValue().stream()
                    .map(AuditRecordVO::getBizId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            Map<Long, AuditBizSummaryVO> summaries = handler.loadSummaries(bizIds);
            for (AuditRecordVO vo : entry.getValue()) {
                applySummary(vo, summaries.get(vo.getBizId()));
            }
        }
    }

    /**
     * 把业务摘要写进 VO；摘要为空说明业务行已被删除
     */
    private void applySummary(AuditRecordVO vo, AuditBizSummaryVO summary) {
        if (summary == null) {
            vo.setBizDeleted(true);
            vo.setBizName("数据已删除");
            return;
        }
        vo.setBizDeleted(false);
        vo.setBizName(summary.getBizName());
        vo.setBizCode(summary.getBizCode());
        vo.setBizStatus(summary.getBizStatus());
        vo.setBizStatusName(summary.getBizStatusName());
    }

    /**
     * 实体转列表 VO（只转审批任务自身字段，业务摘要由 fillBizSummary 回填）
     */
    private AuditRecordVO toVO(AuditRecord record) {
        AuditRecordVO vo = new AuditRecordVO();
        vo.setAuditId(record.getAuditId());
        vo.setBizType(record.getBizType());
        BizType type = BizType.getByCode(record.getBizType());
        vo.setBizTypeName(type == null ? null : type.getDesc());
        vo.setBizId(record.getBizId());
        vo.setStatus(record.getStatus());
        vo.setStatusName(AuditStatus.getNameByCode(record.getStatus()));
        vo.setSubmitUserId(record.getSubmitUserId());
        vo.setCreateTime(record.getCreateTime());
        vo.setAuditUserId(record.getAuditUserId());
        vo.setAuditTime(record.getAuditTime());
        vo.setRejectReason(record.getRejectReason());
        return vo;
    }
}
