package module.template.service;

import java.util.List;

import module.template.dto.ParamTemplateFieldSaveDTO;
import module.template.dto.ParamTemplateFieldUpdateDTO;
import module.template.vo.ParamTemplateFieldListVO;

public interface ParamTemplateFieldService {

    List<ParamTemplateFieldListVO> queryTemplateFieldList(Long templateId);

    void addTemplateField(ParamTemplateFieldSaveDTO paramTemplateFieldDTO);

    void updateTemplateField(ParamTemplateFieldUpdateDTO paramTemplateFieldUpdateDTO);

    void deleteTemplateField(Long fieldId);
    
}
