package module.review.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import common.enums.BizType;
import module.review.dto.AuditApproveDTO;
import module.review.dto.AuditRejectDTO;
import module.review.dto.AuditTaskQueryDTO;
import module.review.vo.AuditDetailVO;
import module.review.vo.AuditRecordVO;

/**
 * 通用审批中心业务接口
 * <p>
 * 前半部分是给业务模块调用的写接口（提交 / 撤回 / 引用检查），
 * 后半部分是审批中心自身的查询与审批接口。
 */
public interface AuditTaskService {

    // ==================== 业务模块调用 ====================

    /**
     * 提交审核：新建一条待审核任务
     * <p>调用方须已在同一事务内把业务行 CAS 到「待审核」。
     *
     * @param bizType 业务类型
     * @param bizId   业务对象主键
     */
    void createPendingTask(BizType bizType, Long bizId);

    /**
     * 撤回：把该业务最近一条「已通过」的审批记录标记为「已撤回」
     *
     * @param bizType 业务类型
     * @param bizId   业务对象主键
     */
    void revokeLatestApproved(BizType bizType, Long bizId);

    /**
     * 撤销申请：把该业务待审核的任务标记为「已撤回」
     *
     * @param bizType 业务类型
     * @param bizId   业务对象主键
     */
    void revokeLatestPending(BizType bizType, Long bizId);

    /**
     * 统计某业务对象当前待审核的任务条数
     *
     * @param bizType 业务类型
     * @param bizId   业务对象主键
     * @return 待审核条数
     */
    long countPending(BizType bizType, Long bizId);

    // ==================== 审批中心查询与审批 ====================

    /**
     * 分页查询审批任务（status=0 即待办列表）
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    Page<AuditRecordVO> queryAuditPage(AuditTaskQueryDTO dto);

    /**
     * 审批详情：审批任务信息 + 业务明细
     *
     * @param auditId 审批任务主键ID
     * @return 审批详情
     */
    AuditDetailVO queryAuditDetail(Long auditId);

    /**
     * 查询某业务对象的完整审批历史
     *
     * @param bizType 业务类型编码
     * @param bizId   业务对象主键
     * @return 审批记录列表
     */
    List<AuditRecordVO> queryHistory(String bizType, Long bizId);

    /**
     * 审批通过
     *
     * @param dto 审批入参
     */
    void approve(AuditApproveDTO dto);

    /**
     * 审批驳回
     *
     * @param dto 审批入参
     */
    void reject(AuditRejectDTO dto);
}
