package module.review.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import common.enums.BizType;
import module.review.vo.AuditBizSummaryVO;

/**
 * 通用审批中心的业务扩展点。
 * <p>
 * 新增一种可审批业务 = 在 {@link BizType} 加一个常量 + 实现本接口一个类，
 * 无需改动任何表结构与审批中心代码。
 * <p>
 * <b>实现约束（三条都必须遵守）：</b>
 * <ol>
 *   <li>只允许依赖本模块的 Mapper / Entity，<b>禁止依赖本模块的 Service</b>。
 *       否则会与业务 Service 注入 {@code AuditTaskService} 形成构造器循环依赖，
 *       应用启动即失败（业务 Service → 审批中心 → Handler → 业务 Service）。</li>
 *   <li>{@link #markApproved} / {@link #markRejected} 必须使用「带原状态条件的 UPDATE」
 *       （即状态机 CAS），并返回受影响行数；返回 0 表示状态已被他人变更，
 *       审批中心据此判定并发冲突并回滚事务。</li>
 *   <li>不要加 {@code @Transactional}，事务由调用方 {@code AuditTaskServiceImpl} 统一开启，
 *       本接口的实现须加入同一事务。</li>
 * </ol>
 */
public interface BizAuditHandler {

    /**
     * 注册表 key，必须与 {@link BizType} 一一对应。
     * 同一个 BizType 注册多次会在启动时直接抛异常。
     */
    BizType bizType();

    /**
     * 提交审核前置校验：数据完整性、关联有效性等。
     * <p>不通过直接抛 {@code BusinessException}。
     *
     * @param bizId 业务对象主键
     */
    void validateSubmit(Long bizId);

    /**
     * 待办 / 历史列表的一行业务摘要。
     *
     * @param bizId 业务对象主键
     * @return 摘要；业务行已被删除时返回 {@code null}，
     *         审批中心会降级为「数据已删除」，不会让列表接口报错
     */
    AuditBizSummaryVO loadSummary(Long bizId);

    /**
     * 批量摘要，默认逐个调用。
     * <p>列表页 pageSize 最大 100，若实现存在 N+1 问题可覆盖本方法做批量查询。
     *
     * @param bizIds 业务对象主键集合
     * @return bizId -> 摘要，业务行已删除的条目不出现在结果里
     */
    default Map<Long, AuditBizSummaryVO> loadSummaries(Collection<Long> bizIds) {
        Map<Long, AuditBizSummaryVO> map = new HashMap<>();
        if (bizIds == null) {
            return map;
        }
        for (Long id : bizIds) {
            AuditBizSummaryVO summary = loadSummary(id);
            if (summary != null) {
                map.put(id, summary);
            }
        }
        return map;
    }

    /**
     * 审批详情中的业务明细，返回各业务模块自己的 DetailVO。
     * <p>返回类型故意声明为 {@code Object}，使审批中心无需依赖任何业务 VO 类型。
     *
     * @param bizId 业务对象主键
     * @return 业务明细，业务行已删除时返回 {@code null}
     */
    Object loadDetail(Long bizId);

    /**
     * 审批通过：CAS 置为「已发布 / 有效」并写入审批人。
     *
     * @param bizId       业务对象主键
     * @param auditUserId 审批人用户ID
     * @return 受影响行数，0 表示状态已被变更（并发冲突）
     */
    int markApproved(Long bizId, Long auditUserId);

    /**
     * 审批驳回：CAS 置回「草稿」并清空审批人。
     *
     * @param bizId       业务对象主键
     * @param auditUserId 审批人用户ID
     * @return 受影响行数，0 表示状态已被变更（并发冲突）
     */
    int markRejected(Long bizId, Long auditUserId);
}
