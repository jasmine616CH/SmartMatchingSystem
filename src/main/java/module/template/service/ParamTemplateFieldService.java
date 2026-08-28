package module.template.service;

import java.util.List;

import module.system.vo.DictOptionVO;
import module.template.dto.ParamTemplateFieldSaveDTO;
import module.template.dto.ParamTemplateFieldUpdateDTO;
import module.template.vo.CurrentTemplateFieldListVO;
import module.template.vo.ParamTemplateFieldListVO;
import module.template.vo.ParamTemplateFieldVO;

public interface ParamTemplateFieldService {

    List<ParamTemplateFieldListVO> queryTemplateFieldList(Long templateId);

    ParamTemplateFieldVO queryTemplateFieldDetail(Long fieldId);

    List<CurrentTemplateFieldListVO> queryCurrentTemplateFieldList(Long templateId);

    List<DictOptionVO> queryEnumParamValues(Long fieldId);

    void addTemplateField(ParamTemplateFieldSaveDTO paramTemplateFieldDTO);

    void updateTemplateField(ParamTemplateFieldUpdateDTO paramTemplateFieldUpdateDTO);

    void deleteTemplateField(Long fieldId);
    
}
