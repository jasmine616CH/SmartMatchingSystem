package module.template.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import common.enums.TemplateStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
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
        paramTemplate.setStatus(TemplateStatus.DRAFT.getCode());
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
        if (TemplateStatus.PUBLISHED.getCode().equals(status)) {
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

    @Override
    public void submitAudit(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        // 1.校验参数、查询数据
        ParamTemplate template = paramTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "模板不存在");
        }
        // 仅草稿【0】允许提交审核
        if (!TemplateStatus.DRAFT.getCode().equals(template.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "仅草稿状态的模板允许提交审核");
        }
        // 修改状态：草稿 → 待审核
        ParamTemplate updateEntity = new ParamTemplate();
        updateEntity.setTemplateId(templateId);
        updateEntity.setStatus(TemplateStatus.PENDING_AUDIT.getCode());
        paramTemplateMapper.updateById(updateEntity);
    }

    @Override
    public void revoke(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        ParamTemplate template = paramTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "模板不存在");
        }
        // 仅已发布【2】支持撤回
        if (!TemplateStatus.PUBLISHED.getCode().equals(template.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN, "仅已发布状态的模板可执行撤回操作");
        }
        // 状态变更：已发布 → 待审核
        ParamTemplate updateEntity = new ParamTemplate();
        updateEntity.setTemplateId(templateId);
        updateEntity.setStatus(TemplateStatus.PENDING_AUDIT.getCode());
        paramTemplateMapper.updateById(updateEntity);
    }

}
