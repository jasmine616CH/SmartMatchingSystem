package module.template.spi;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.enums.BizType;
import common.enums.PublishStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.review.spi.BizAuditHandler;
import module.review.vo.AuditBizSummaryVO;
import module.template.entity.ParamTemplate;
import module.template.entity.ParamTemplateField;
import module.template.mapper.ParamTemplateFieldMapper;
import module.template.mapper.ParamTemplateMapper;
import module.template.vo.ParamTemplateDetailVO;
import module.category.entity.PartCategory;
import module.category.mapper.PartCategoryMapper;

/**
 * 配件参数模板的审批扩展点
 * <p>
 * 只依赖 Mapper，不依赖本模块 Service——否则会与
 * ParamTemplateService → AuditTaskService → 本类 构成构造器循环依赖。
 */
@Component
@RequiredArgsConstructor
public class ParamTemplateBizAuditHandler implements BizAuditHandler {

    private final ParamTemplateMapper paramTemplateMapper;

    private final ParamTemplateFieldMapper paramTemplateFieldMapper;

    private final PartCategoryMapper partCategoryMapper;

    @Override
    public BizType bizType() {
        return BizType.PARAM_TEMPLATE;
    }

    /**
     * 提交审核前置校验：模板存在、所属分类已发布、至少有一个参数字段
     */
    @Override
    public void validateSubmit(Long bizId) {
        ParamTemplate template = paramTemplateMapper.selectById(bizId);
        if (template == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "参数模板不存在");
        }
        checkCategoryPublished(template.getCatId());

        LambdaQueryWrapper<ParamTemplateField> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParamTemplateField::getTemplateId, bizId);
        if (paramTemplateFieldMapper.selectCount(wrapper) == 0) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL,
                    "参数模板下没有任何参数字段，无法提交审核");
        }
    }

    @Override
    public AuditBizSummaryVO loadSummary(Long bizId) {
        ParamTemplate template = paramTemplateMapper.selectById(bizId);
        if (template == null) {
            return null;
        }
        AuditBizSummaryVO summary = new AuditBizSummaryVO();
        summary.setBizName(template.getTemplateName());
        summary.setBizCode(template.getVersion());
        summary.setBizStatus(template.getStatus());
        summary.setBizStatusName(PublishStatus.getNameByCode(template.getStatus()));
        return summary;
    }

    @Override
    public Object loadDetail(Long bizId) {
        ParamTemplate template = paramTemplateMapper.selectById(bizId);
        if (template == null) {
            return null;
        }
        ParamTemplateDetailVO detail = BeanUtil.toBean(template, ParamTemplateDetailVO.class);
        return detail;
    }

    /**
     * 审批通过：待审核 → 已发布，并记录审批人
     */
    @Override
    public int markApproved(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<ParamTemplate> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ParamTemplate::getTemplateId, bizId)
                .eq(ParamTemplate::getStatus, PublishStatus.PENDING_AUDIT.getCode())
                .set(ParamTemplate::getStatus, PublishStatus.PUBLISHED.getCode())
                .set(ParamTemplate::getAuditUserId, auditUserId);
        return paramTemplateMapper.update(null, wrapper);
    }

    /**
     * 审批驳回：待审核 → 草稿，并清空审批人
     */
    @Override
    public int markRejected(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<ParamTemplate> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ParamTemplate::getTemplateId, bizId)
                .eq(ParamTemplate::getStatus, PublishStatus.PENDING_AUDIT.getCode())
                .set(ParamTemplate::getStatus, PublishStatus.DRAFT.getCode())
                .set(ParamTemplate::getAuditUserId, null);
        return paramTemplateMapper.update(null, wrapper);
    }

    /**
     * 模板绑定的三级分类必须已发布，否则模板发布出去也没有可用的配件
     */
    private void checkCategoryPublished(Long catId) {
        if (catId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "模板未绑定配件分类");
        }
        PartCategory category = partCategoryMapper.selectById(catId);
        if (category == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "所属配件分类不存在");
        }
        if (!PublishStatus.PUBLISHED.getCode().equals(category.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "所属分类【" + category.getCatName() + "】未发布，参数模板无法提交审核");
        }
    }
}
