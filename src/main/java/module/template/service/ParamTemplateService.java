package module.template.service;


import module.template.dto.ParamTemplateSaveDTO;
import module.template.dto.ParamTemplateUpdateDTO;
import module.template.vo.ParamTemplateBriefVO;
import module.template.vo.ParamTemplateDetailVO;

public interface ParamTemplateService {

    ParamTemplateBriefVO queryTemplateList(Long catId);

    ParamTemplateDetailVO queryTemplateDetail(Long templateId);

    void addTemplate(ParamTemplateSaveDTO paramTemplateSaveDTO);

    void updateTemplate(ParamTemplateUpdateDTO paramTemplateUpdateDTO);

    void deleteTemplate(Long templateId);

    void submitAudit(Long templateId);

    void revoke(Long templateId);
}
