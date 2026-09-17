package module.part.spi;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.enums.BizType;
import common.enums.PublishStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.category.entity.PartCategory;
import module.category.mapper.PartCategoryMapper;
import module.part.entity.PartInfo;
import module.part.mapper.PartInfoMapper;
import module.part.vo.PartInfoListVO;
import module.review.spi.BizAuditHandler;
import module.review.vo.AuditBizSummaryVO;
import module.template.entity.ParamTemplate;
import module.template.mapper.ParamTemplateMapper;

/**
 * 配件档案的审批扩展点
 * <p>
 * 只依赖 Mapper，不依赖本模块 Service（循环依赖约束见 BizAuditHandler javadoc）。
 */
@Component
@RequiredArgsConstructor
public class PartInfoBizAuditHandler implements BizAuditHandler {

    private final PartInfoMapper partInfoMapper;

    private final PartCategoryMapper partCategoryMapper;

    private final ParamTemplateMapper paramTemplateMapper;

    @Override
    public BizType bizType() {
        return BizType.PART_INFO;
    }

    /**
     * 提交审核前置校验：档案存在且有物料编码、所属分类与参数模板均已发布
     */
    @Override
    public void validateSubmit(Long bizId) {
        PartInfo part = partInfoMapper.selectById(bizId);
        if (part == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件档案不存在");
        }
        if (!StringUtils.hasText(part.getPartCode())) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL,
                    "配件缺少物料编码，无法提交审核");
        }
        checkCategoryPublished(part.getCatId());
        checkTemplatePublished(part.getTemplateId());
    }

    @Override
    public AuditBizSummaryVO loadSummary(Long bizId) {
        PartInfo part = partInfoMapper.selectById(bizId);
        if (part == null) {
            return null;
        }
        AuditBizSummaryVO summary = new AuditBizSummaryVO();
        // 配件档案没有名称列，用「品牌 + 型号」作为展示名
        summary.setBizName(part.getBrand() + " " + part.getModel());
        summary.setBizCode(part.getPartCode());
        summary.setBizStatus(part.getPublishingStatus());
        summary.setBizStatusName(PublishStatus.getNameByCode(part.getPublishingStatus()));
        return summary;
    }

    @Override
    public Object loadDetail(Long bizId) {
        PartInfo part = partInfoMapper.selectById(bizId);
        if (part == null) {
            return null;
        }
        PartInfoListVO detail = BeanUtil.toBean(part, PartInfoListVO.class);
        return detail;
    }

    /**
     * 审批通过：待审核 → 已发布，并记录审批人
     */
    @Override
    public int markApproved(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<PartInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PartInfo::getPartId, bizId)
                .eq(PartInfo::getPublishingStatus, PublishStatus.PENDING_AUDIT.getCode())
                .set(PartInfo::getPublishingStatus, PublishStatus.PUBLISHED.getCode())
                .set(PartInfo::getAuditUserId, auditUserId);
        return partInfoMapper.update(null, wrapper);
    }

    /**
     * 审批驳回：待审核 → 草稿，并清空审批人
     */
    @Override
    public int markRejected(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<PartInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PartInfo::getPartId, bizId)
                .eq(PartInfo::getPublishingStatus, PublishStatus.PENDING_AUDIT.getCode())
                .set(PartInfo::getPublishingStatus, PublishStatus.DRAFT.getCode())
                .set(PartInfo::getAuditUserId, null);
        return partInfoMapper.update(null, wrapper);
    }

    private void checkCategoryPublished(Long catId) {
        if (catId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "配件未绑定分类");
        }
        PartCategory category = partCategoryMapper.selectById(catId);
        if (category == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "所属配件分类不存在");
        }
        if (!PublishStatus.PUBLISHED.getCode().equals(category.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "所属分类【" + category.getCatName() + "】未发布，配件无法提交审核");
        }
    }

    private void checkTemplatePublished(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "配件未绑定参数模板");
        }
        ParamTemplate template = paramTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "所属参数模板不存在");
        }
        if (!PublishStatus.PUBLISHED.getCode().equals(template.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "所属参数模板【" + template.getTemplateName() + "】未发布，配件无法提交审核");
        }
    }
}
