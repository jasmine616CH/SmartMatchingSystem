package module.supplier.spi;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.enums.BizType;
import common.enums.SupplierQualificationStatus;
import common.enums.SupplierStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.review.spi.BizAuditHandler;
import module.review.vo.AuditBizSummaryVO;
import module.supplier.entity.Supplier;
import module.supplier.entity.SupplierQualification;
import module.supplier.mapper.SupplierMapper;
import module.supplier.mapper.SupplierQualificationMapper;
import module.supplier.vo.SupplierQualificationVO;

/**
 * 供应商资质的审批扩展点
 * <p>
 * 只依赖本模块的 Mapper，不依赖 SupplierQualificationService（循环依赖）。
 */
@Component
@RequiredArgsConstructor
public class SupplierQualificationBizAuditHandler implements BizAuditHandler {

    private final SupplierQualificationMapper supplierQualificationMapper;

    private final SupplierMapper supplierMapper;

    @Override
    public BizType bizType() {
        return BizType.SUPPLIER_QUALIFICATION;
    }

    /**
     * 提交审核前置校验：资质存在、未过期、所属供应商已发布
     */
    @Override
    public void validateSubmit(Long bizId) {
        SupplierQualification qf = supplierQualificationMapper.selectById(bizId);
        if (qf == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
        if (qf.getExpireDate() != null && qf.getExpireDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ResultCode.PARAM_RANGE_ERROR,
                    "资质已过失效日期，无法提交审核");
        }
        Supplier supplier = supplierMapper.selectById(qf.getSupplierId());
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND, "所属供应商不存在");
        }
        if (!SupplierStatus.PUBLISHED.getCode().equals(supplier.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "所属供应商未发布，资质无法提交审核");
        }
    }

    /**
     * 待办/历史列表的一行业务摘要；业务行已删除返回 null
     */
    @Override
    public AuditBizSummaryVO loadSummary(Long bizId) {
        SupplierQualification qf = supplierQualificationMapper.selectById(bizId);
        if (qf == null) {
            return null;
        }
        AuditBizSummaryVO summary = new AuditBizSummaryVO();
        summary.setBizName(qf.getQfName());
        summary.setBizCode(qf.getCertNo());
        // 展示用状态：已过失效日期的有效证书投影为「过期」
        Integer display = SupplierQualificationStatus.resolveDisplayStatus(
                qf.getStatus(), qf.getExpireDate());
        summary.setBizStatus(display);
        summary.setBizStatusName(SupplierQualificationStatus.getNameByCode(display));
        return summary;
    }

    /**
     * 审批详情中的业务明细
     */
    @Override
    public Object loadDetail(Long bizId) {
        SupplierQualification qf = supplierQualificationMapper.selectById(bizId);
        if (qf == null) {
            return null;
        }
        SupplierQualificationVO vo = BeanUtil.toBean(qf, SupplierQualificationVO.class);
        vo.setStoredStatus(qf.getStatus());
        vo.setStatus(SupplierQualificationStatus.resolveDisplayStatus(
                qf.getStatus(), qf.getExpireDate()));
        vo.setStatusName(SupplierQualificationStatus.getNameByCode(vo.getStatus()));

        Supplier supplier = supplierMapper.selectById(qf.getSupplierId());
        if (supplier != null) {
            vo.setSupplierName(supplier.getSupplierName());
        }
        return vo;
    }

    /**
     * 审批通过：待审核 → 有效，并记录审批人
     * <p>永远写「有效」，绝不写「过期」——过期是读时派生的投影。
     */
    @Override
    public int markApproved(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<SupplierQualification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SupplierQualification::getQfId, bizId)
                .eq(SupplierQualification::getStatus,
                        SupplierQualificationStatus.PENDING_AUDIT.getCode())
                .set(SupplierQualification::getStatus,
                        SupplierQualificationStatus.VALID.getCode())
                .set(SupplierQualification::getAuditUserId, auditUserId);
        return supplierQualificationMapper.update(null, wrapper);
    }

    /**
     * 审批驳回：待审核 → 草稿，并清空审批人
     */
    @Override
    public int markRejected(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<SupplierQualification> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SupplierQualification::getQfId, bizId)
                .eq(SupplierQualification::getStatus,
                        SupplierQualificationStatus.PENDING_AUDIT.getCode())
                .set(SupplierQualification::getStatus,
                        SupplierQualificationStatus.DRAFT.getCode())
                .set(SupplierQualification::getAuditUserId, null);
        return supplierQualificationMapper.update(null, wrapper);
    }
}
