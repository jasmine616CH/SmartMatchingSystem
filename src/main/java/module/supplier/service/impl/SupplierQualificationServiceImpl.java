package module.supplier.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import common.enums.BizType;
import common.enums.SupplierQualificationStatus;
import common.enums.SupplierStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.review.service.AuditTaskService;
import module.supplier.dto.SupplierQualificationQueryDTO;
import module.supplier.dto.SupplierQualificationSaveDTO;
import module.supplier.dto.SupplierQualificationUpdateDTO;
import module.supplier.entity.Supplier;
import module.supplier.entity.SupplierQualification;
import module.supplier.mapper.SupplierMapper;
import module.supplier.mapper.SupplierQualificationMapper;
import module.supplier.service.SupplierQualificationService;
import module.supplier.vo.SupplierQualificationVO;

/**
 * 供应商资质业务实现类
 * 实现供应商资质的增删改查与审核流转
 */
@RequiredArgsConstructor
@Service
public class SupplierQualificationServiceImpl implements SupplierQualificationService {

    private final SupplierQualificationMapper supplierQualificationMapper;

    private final SupplierMapper supplierMapper;

    private final AuditTaskService auditTaskService;

    /**
     * 分页查询供应商资质列表
     */
    @Override
    public Page<SupplierQualificationVO> queryQualificationList(SupplierQualificationQueryDTO dto) {
        SupplierQualificationQueryDTO query =
                dto == null ? new SupplierQualificationQueryDTO() : dto;

        Page<SupplierQualificationVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SupplierQualificationVO> result =
                supplierQualificationMapper.selectQualificationPage(page, query);
        result.getRecords().forEach(this::applyDisplayStatus);
        return result;
    }

    /**
     * 查询供应商资质详情
     */
    @Override
    public SupplierQualificationVO queryQualificationDetail(Long qfId) {
        if (qfId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "qfId 不能为空");
        }
        SupplierQualification entity = supplierQualificationMapper.selectById(qfId);
        if (entity == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
        SupplierQualificationVO vo = BeanUtil.toBean(entity, SupplierQualificationVO.class);
        applyDisplayStatus(vo);
        // 详情额外带出所属供应商名称
        Supplier supplier = supplierMapper.selectById(entity.getSupplierId());
        if (supplier != null) {
            vo.setSupplierName(supplier.getSupplierName());
        }
        return vo;
    }

    /**
     * 新增供应商资质，初始状态为草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addQualification(SupplierQualificationSaveDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        if (dto.getSupplierId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "supplierId 不能为空");
        }
        checkOwningSupplierPublished(dto.getSupplierId(), "新增资质");
        // 允许补录已过期的历史证书（只会立即展示为「过期」），但提交审核时会被拦下
        checkDateRange(dto.getEffectDate(), dto.getExpireDate());

        String certNo = normalizeNullable(dto.getCertNo());
        checkCertNoDuplicate(dto.getSupplierId(), certNo, null);

        SupplierQualification entity = BeanUtil.toBean(dto, SupplierQualification.class);
        entity.setCertNo(certNo);
        entity.setStatus(SupplierQualificationStatus.DRAFT.getCode());
        entity.setAuditUserId(null);

        int rows = supplierQualificationMapper.insert(entity);
        if (rows == 0) {
            throw new BusinessException(ResultCode.ERROR, "新增资质失败");
        }
    }

    /**
     * 修改供应商资质
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQualification(SupplierQualificationUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        Long qfId = dto.getQfId();
        if (qfId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "qfId 不能为空");
        }
        SupplierQualification db = supplierQualificationMapper.selectById(qfId);
        if (db == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
        if (!db.getSupplierId().equals(dto.getSupplierId())) {
            throw new BusinessException(ResultCode.PARAM_VALUE_INVALID, "不允许变更资质所属供应商");
        }
        checkEditable(db.getStatus());
        checkDateRange(dto.getEffectDate(), dto.getExpireDate());

        String certNo = normalizeNullable(dto.getCertNo());
        checkCertNoDuplicate(dto.getSupplierId(), certNo, qfId);

        SupplierQualification entity = BeanUtil.toBean(dto, SupplierQualification.class);
        entity.setCertNo(certNo);
        // status 与 auditUserId 不允许通过修改接口变更
        entity.setStatus(null);
        entity.setAuditUserId(null);

        int rows = supplierQualificationMapper.updateById(entity);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
    }

    /**
     * 删除供应商资质（仅草稿状态可删）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQualification(Long qfId) {
        if (qfId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "qfId 不能为空");
        }
        SupplierQualification db = supplierQualificationMapper.selectById(qfId);
        if (db == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
        if (!SupplierQualificationStatus.DRAFT.getCode().equals(db.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅草稿状态的资质允许删除");
        }
        if (auditTaskService.countPending(BizType.SUPPLIER_QUALIFICATION, qfId) > 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该资质存在待审核任务，请先撤回后再删除");
        }

        int rows = supplierQualificationMapper.deleteById(qfId);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
    }

    /**
     * 提交审核：草稿 → 待审核
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAudit(Long qfId) {
        if (qfId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "qfId 不能为空");
        }
        SupplierQualification db = supplierQualificationMapper.selectById(qfId);
        if (db == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
        if (!SupplierQualificationStatus.DRAFT.getCode().equals(db.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅草稿状态的资质允许提交审核");
        }
        // 不让审批人去审一张已经死掉的证书
        if (db.getExpireDate() != null && db.getExpireDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ResultCode.PARAM_RANGE_ERROR,
                    "资质已过失效日期，无法提交审核");
        }
        checkOwningSupplierPublished(db.getSupplierId(), "提交审核");

        updateStatusCas(qfId, SupplierQualificationStatus.DRAFT,
                SupplierQualificationStatus.PENDING_AUDIT, false, null);
        auditTaskService.createPendingTask(BizType.SUPPLIER_QUALIFICATION, qfId);
    }

    /**
     * 撤回：有效 → 草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long qfId) {
        if (qfId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "qfId 不能为空");
        }
        SupplierQualification db = supplierQualificationMapper.selectById(qfId);
        if (db == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
        if (!SupplierQualificationStatus.VALID.getCode().equals(db.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅有效状态的资质允许撤回");
        }
        // 撤回后清空审批人
        updateStatusCas(qfId, SupplierQualificationStatus.VALID,
                SupplierQualificationStatus.DRAFT, true, null);
        auditTaskService.revokeLatestApproved(BizType.SUPPLIER_QUALIFICATION, qfId);
    }

    /**
     * 撤销申请：待审核 → 草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSubmit(Long qfId) {
        if (qfId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "qfId 不能为空");
        }
        SupplierQualification db = supplierQualificationMapper.selectById(qfId);
        if (db == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
        if (!SupplierQualificationStatus.PENDING_AUDIT.getCode().equals(db.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅待审核状态的资质允许撤销申请");
        }
        updateStatusCas(qfId, SupplierQualificationStatus.PENDING_AUDIT,
                SupplierQualificationStatus.DRAFT, false, null);
        auditTaskService.revokeLatestPending(BizType.SUPPLIER_QUALIFICATION, qfId);
    }

    // ==================== 包内可见：供 SPI 处理器复用 ====================

    /**
     * 带原状态条件地更新资质状态（状态机 CAS）
     * <p>
     * audit_user_id 必须用 {@code .set(...)} 显式赋值：若走实体 + updateById，
     * NOT_NULL 策略会把 null 静默跳过，导致驳回/撤回时清空不掉审批人。
     *
     * @param qfId           资质主键ID
     * @param from           期望的原状态
     * @param to             目标状态
     * @param touchAuditUser 是否同时写 audit_user_id
     * @param auditUserId    审批人用户ID，touchAuditUser 为 true 时生效（null 表示清空）
     * @return 受影响行数，0 表示状态已被他人变更
     */
    int updateStatusCas(Long qfId, SupplierQualificationStatus from,
            SupplierQualificationStatus to, boolean touchAuditUser, Long auditUserId) {
        LambdaUpdateWrapper<SupplierQualification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SupplierQualification::getQfId, qfId)
                .eq(SupplierQualification::getStatus, from.getCode())
                .set(SupplierQualification::getStatus, to.getCode());
        if (touchAuditUser) {
            wrapper.set(SupplierQualification::getAuditUserId, auditUserId);
        }
        return supplierQualificationMapper.update(null, wrapper);
    }

    // ==================== 私有方法 ====================

    /**
     * 把数据库状态投影为展示状态（有效且已过失效日期 → 过期）
     */
    private void applyDisplayStatus(SupplierQualificationVO vo) {
        vo.setStoredStatus(vo.getStatus());
        vo.setStatus(SupplierQualificationStatus.resolveDisplayStatus(
                vo.getStatus(), vo.getExpireDate()));
        vo.setStatusName(SupplierQualificationStatus.getNameByCode(vo.getStatus()));
    }

    /**
     * 校验当前状态是否允许编辑
     */
    private void checkEditable(Integer status) {
        if (SupplierQualificationStatus.PENDING_AUDIT.getCode().equals(status)) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "资质处于【待审核】状态，暂不支持编辑，请先等待审批或撤销申请");
        }
        if (SupplierQualificationStatus.VALID.getCode().equals(status)) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "资质处于【有效】状态，暂不支持编辑，请先执行撤回操作");
        }
    }

    /**
     * 校验所属供应商存在且已发布
     * <p>资质依附于已发布的供应商，供应商未发布时不允许维护其资质。
     */
    private void checkOwningSupplierPublished(Long supplierId, String action) {
        Supplier supplier = supplierMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND, "所属供应商不存在");
        }
        if (!SupplierStatus.PUBLISHED.getCode().equals(supplier.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "所属供应商未发布，" + action + "失败");
        }
    }

    /**
     * 校验证书生效/失效日期
     */
    private void checkDateRange(LocalDate effectDate, LocalDate expireDate) {
        if (effectDate == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "effectDate 不能为空");
        }
        if (expireDate == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "expireDate 不能为空");
        }
        if (!expireDate.isAfter(effectDate)) {
            throw new BusinessException(ResultCode.PARAM_RANGE_ERROR, "失效日期必须晚于生效日期");
        }
    }

    /**
     * 校验同一供应商下证书编号唯一
     *
     * @param excludeQfId 修改场景下需排除的自身主键ID，新增时传 null
     */
    private void checkCertNoDuplicate(Long supplierId, String certNo, Long excludeQfId) {
        if (certNo == null) {
            return;
        }
        LambdaQueryWrapper<SupplierQualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierQualification::getSupplierId, supplierId)
                .eq(SupplierQualification::getCertNo, certNo);
        if (excludeQfId != null) {
            wrapper.ne(SupplierQualification::getQfId, excludeQfId);
        }
        if (supplierQualificationMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE,
                    "该供应商下证书编号【" + certNo + "】已存在");
        }
    }

    /**
     * 把空白字符串规整为 null（唯一索引允许多个 NULL，但空串会撞键）
     */
    private String normalizeNullable(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
