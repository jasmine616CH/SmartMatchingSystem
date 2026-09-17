package module.template.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import common.enums.PublishStatus;
import common.exception.BusinessException;
import common.enums.BizType;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.review.service.AuditTaskService;
import module.template.dto.ParamTemplateSaveDTO;
import module.template.dto.ParamTemplateUpdateDTO;
import module.template.entity.ParamTemplate;
import module.template.mapper.ParamTemplateMapper;
import module.template.service.ParamTemplateService;
import module.template.vo.ParamTemplateBriefVO;
import module.template.vo.ParamTemplateDetailVO;

@RequiredArgsConstructor
@Service
public class ParamTemplateServiceImpl implements ParamTemplateService {

    private final ParamTemplateMapper paramTemplateMapper;

    private final AuditTaskService auditTaskService;

    @Override
    public ParamTemplateBriefVO queryTemplateList(Long catId) {
        QueryWrapper<ParamTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_id", catId);
        ParamTemplate paramTemplate = paramTemplateMapper.selectOne(queryWrapper);
        if (paramTemplate == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        ParamTemplateBriefVO paramTemplateBriefVO = new ParamTemplateBriefVO();
        BeanUtils.copyProperties(paramTemplate, paramTemplateBriefVO);
        return paramTemplateBriefVO;
    }

    @Override
    public ParamTemplateDetailVO queryTemplateDetail(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        ParamTemplate paramTemplate = paramTemplateMapper.getTemplateDetailWithAuditName(templateId);
        if (paramTemplate == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        ParamTemplateDetailVO paramTemplateDetailVO = new ParamTemplateDetailVO();
        BeanUtils.copyProperties(paramTemplate, paramTemplateDetailVO);

        return paramTemplateDetailVO;
    }

    @Override
    public void addTemplate(ParamTemplateSaveDTO paramTemplateSaveDTO) {
        if (paramTemplateSaveDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        String templateName = paramTemplateSaveDTO.getTemplateName();
        long existCount = paramTemplateMapper.countByTemplateName(templateName);
        if (existCount > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, templateName + "已存在");
        }

        ParamTemplate paramTemplate = new ParamTemplate();
        BeanUtils.copyProperties(paramTemplateSaveDTO, paramTemplate);
        paramTemplate.setStatus(PublishStatus.DRAFT.getCode());
        paramTemplateMapper.insert(paramTemplate);
    }

    @Override
    public void updateTemplate(ParamTemplateUpdateDTO paramTemplateUpdateDTO) {
        if (paramTemplateUpdateDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        Long templateId = paramTemplateUpdateDTO.getTemplateId();
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "模板ID不能为空");
        }

        ParamTemplate dbTemplate = paramTemplateMapper.selectById(templateId);
        if (dbTemplate == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "模板不存在");
        }

        Integer status = dbTemplate.getStatus();
        if (PublishStatus.PUBLISHED.getCode().equals(status)) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "模板处于【已发布】状态，暂不支持编辑，请先执行撤回操作");
        }

        String templateName = paramTemplateUpdateDTO.getTemplateName();

        QueryWrapper<ParamTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("template_name", templateName);
        queryWrapper.ne("template_id", templateId);

        long count = paramTemplateMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_DUPLICATE, "模板名：" + templateName + "已存在");
        }

        ParamTemplate paramTemplate = new ParamTemplate();
        BeanUtils.copyProperties(paramTemplateUpdateDTO, paramTemplate);
        paramTemplateMapper.updateById(paramTemplate);
    }

    @Override
    public void deleteTemplate(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        QueryWrapper<ParamTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("template_id", templateId);
        long count = paramTemplateMapper.selectCount(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "模板不存在");
        }
        paramTemplateMapper.deleteById(templateId);
    }

    /**
     * 提交审核：草稿 → 待审核
     * <p>
     * 与供应商模块一致：先 CAS 业务行，再在审批中心建待办任务，
     * 使审批人能在待办列表里看到它（老实现只改 status，不进审批中心）。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAudit(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        ParamTemplate template = paramTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "模板不存在");
        }
        if (!PublishStatus.DRAFT.getCode().equals(template.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "仅草稿状态的模板允许提交审核");
        }
        updateStatusCas(templateId, PublishStatus.DRAFT, PublishStatus.PENDING_AUDIT);
        auditTaskService.createPendingTask(BizType.PARAM_TEMPLATE, templateId);
    }

    /**
     * 撤回：已发布 → 草稿
     * <p>
     * 老实现是落到「待审核」但<b>不建待办任务</b>，那条记录会永远卡住：
     * 审批人看不到它，而 revoke 又只允许从已发布触发，等于没有出口。
     * 现在统一落到草稿，并保持「状态为待审核 ⟺ 存在一条待审核任务」的不变量。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        ParamTemplate template = paramTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "模板不存在");
        }
        if (!PublishStatus.PUBLISHED.getCode().equals(template.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "仅已发布状态的模板可执行撤回操作");
        }
        updateStatusCasAndAudit(templateId, PublishStatus.PUBLISHED, PublishStatus.DRAFT, null);
        auditTaskService.revokeLatestApproved(BizType.PARAM_TEMPLATE, templateId);
    }

    /**
     * 撤销申请：待审核 → 草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSubmit(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        ParamTemplate template = paramTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "模板不存在");
        }
        if (!PublishStatus.PENDING_AUDIT.getCode().equals(template.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "仅待审核状态的模板允许撤销申请");
        }
        updateStatusCas(templateId, PublishStatus.PENDING_AUDIT, PublishStatus.DRAFT);
        auditTaskService.revokeLatestPending(BizType.PARAM_TEMPLATE, templateId);
    }

    /**
     * 带原状态条件地更新模板状态（状态机 CAS），不动审批人字段
     */
    private void updateStatusCas(Long templateId, PublishStatus from, PublishStatus to) {
        updateStatusCasAndAudit(templateId, from, to, null, false);
    }

    /**
     * 带原状态条件地更新模板状态，并同时写审批人
     *
     * @param touchAuditUser 是否同时写 audit_user_id（撤回/驳回要清空，提交不碰）
     */
    private void updateStatusCasAndAudit(Long templateId, PublishStatus from, PublishStatus to,
            Long auditUserId) {
        updateStatusCasAndAudit(templateId, from, to, auditUserId, true);
    }

    private void updateStatusCasAndAudit(Long templateId, PublishStatus from, PublishStatus to,
            Long auditUserId, boolean touchAuditUser) {
        LambdaUpdateWrapper<ParamTemplate> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ParamTemplate::getTemplateId, templateId)
                .eq(ParamTemplate::getStatus, from.getCode())
                .set(ParamTemplate::getStatus, to.getCode());
        if (touchAuditUser) {
            wrapper.set(ParamTemplate::getAuditUserId, auditUserId);
        }
        // 实体参数必须传 null：否则 MP 会把实体的非空字段（含主键）一并拼进 SET 子句
        if (paramTemplateMapper.update(null, wrapper) == 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "模板状态已变更，请刷新后重试");
        }
    }

}
