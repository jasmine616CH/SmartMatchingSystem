package module.supplier.spi;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.enums.BizType;
import common.enums.SupplierStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import module.review.spi.BizAuditHandler;
import module.review.vo.AuditBizSummaryVO;
import module.supplier.entity.Supplier;
import module.supplier.entity.SupplierContact;
import module.supplier.mapper.SupplierContactMapper;
import module.supplier.mapper.SupplierMapper;
import module.supplier.vo.SupplierContactVO;
import module.supplier.vo.SupplierDetailVO;
import lombok.RequiredArgsConstructor;

/**
 * 供应商主体的审批扩展点
 * <p>
 * 只依赖本模块的 Mapper，不依赖 SupplierService——否则会与
 * SupplierService → AuditTaskService → 本类 构成构造器循环依赖，启动即失败。
 */
@Component
@RequiredArgsConstructor
public class SupplierBizAuditHandler implements BizAuditHandler {

    private final SupplierMapper supplierMapper;

    private final SupplierContactMapper supplierContactMapper;

    @Override
    public BizType bizType() {
        return BizType.SUPPLIER;
    }

    /**
     * 提交审核前置校验：主体存在、有信用代码、至少一个联系人
     */
    @Override
    public void validateSubmit(Long bizId) {
        Supplier supplier = supplierMapper.selectById(bizId);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }
        if (!StringUtils.hasText(supplier.getCreditCode())) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL,
                    "供应商缺少统一社会信用代码，无法提交审核");
        }
        if (countContacts(bizId) == 0) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL,
                    "供应商缺少联系人，无法提交审核");
        }
    }

    /**
     * 待办/历史列表的一行业务摘要；业务行已删除返回 null
     */
    @Override
    public AuditBizSummaryVO loadSummary(Long bizId) {
        Supplier supplier = supplierMapper.selectById(bizId);
        if (supplier == null) {
            return null;
        }
        AuditBizSummaryVO summary = new AuditBizSummaryVO();
        summary.setBizName(supplier.getSupplierName());
        summary.setBizCode(supplier.getCreditCode());
        summary.setBizStatus(supplier.getStatus());
        summary.setBizStatusName(SupplierStatus.getNameByCode(supplier.getStatus()));
        return summary;
    }

    /**
     * 审批详情中的业务明细
     */
    @Override
    public Object loadDetail(Long bizId) {
        Supplier supplier = supplierMapper.selectById(bizId);
        if (supplier == null) {
            return null;
        }
        SupplierDetailVO detail = BeanUtil.toBean(supplier, SupplierDetailVO.class);
        detail.setStatusName(SupplierStatus.getNameByCode(supplier.getStatus()));

        LambdaQueryWrapper<SupplierContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierContact::getSupplierId, bizId)
                .orderByAsc(SupplierContact::getContactId);
        detail.setContacts(supplierContactMapper.selectList(wrapper).stream()
                .map(c -> BeanUtil.toBean(c, SupplierContactVO.class))
                .toList());
        return detail;
    }

    /**
     * 审批通过：待审核 → 已发布，并记录审批人
     */
    @Override
    public int markApproved(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<Supplier> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Supplier::getSupplierId, bizId)
                .eq(Supplier::getStatus, SupplierStatus.PENDING_AUDIT.getCode())
                .set(Supplier::getStatus, SupplierStatus.PUBLISHED.getCode())
                .set(Supplier::getAuditUserId, auditUserId);
        return supplierMapper.update(null, wrapper);
    }

    /**
     * 审批驳回：待审核 → 草稿，并清空审批人
     */
    @Override
    public int markRejected(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<Supplier> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Supplier::getSupplierId, bizId)
                .eq(Supplier::getStatus, SupplierStatus.PENDING_AUDIT.getCode())
                .set(Supplier::getStatus, SupplierStatus.DRAFT.getCode())
                .set(Supplier::getAuditUserId, null);
        return supplierMapper.update(null, wrapper);
    }

    private long countContacts(Long supplierId) {
        LambdaQueryWrapper<SupplierContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierContact::getSupplierId, supplierId);
        return supplierContactMapper.selectCount(wrapper);
    }
}
