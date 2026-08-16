package module.template.service;

import java.util.List;

import module.template.dto.ParamTemplateSaveDTO;
import module.template.dto.ParamTemplateUpdateDTO;
import module.template.vo.ParamTemplateDetailVO;
import module.template.vo.ParamTemplateListVO;

public interface ParamTemplateService {

    List<ParamTemplateListVO> queryTemplateList(Long catId);

    ParamTemplateDetailVO queryTemplateDetail(Long templateId);

    void addTemplate(ParamTemplateSaveDTO paramTemplateSaveDTO);

    void updateTemplate(ParamTemplateUpdateDTO paramTemplateUpdateDTO);

    void deleteTemplate(Long templateId);

    void submitAudit(Long templateId);

    void revoke(Long templateId);
}
