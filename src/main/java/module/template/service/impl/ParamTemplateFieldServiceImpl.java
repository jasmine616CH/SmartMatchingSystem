package module.template.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.template.dto.ParamTemplateFieldSaveDTO;
import module.template.dto.ParamTemplateFieldUpdateDTO;
import module.template.entity.ParamTemplateField;
import module.template.mapper.ParamTemplateFieldMapper;
import module.template.service.ParamTemplateFieldService;
import module.template.vo.ParamTemplateFieldListVO;
import module.template.vo.ParamTemplateFieldVO;

@RequiredArgsConstructor
@Service
public class ParamTemplateFieldServiceImpl implements ParamTemplateFieldService {

    private final ParamTemplateFieldMapper paramTemplateFieldMapper;

    @Override
    public List<ParamTemplateFieldListVO> queryTemplateFieldList(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        QueryWrapper<ParamTemplateField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("template_id", templateId);
        List<ParamTemplateField> paramTemplateFieldList = paramTemplateFieldMapper.selectList(queryWrapper);

        List<ParamTemplateFieldListVO> paramTemplateFieldListVOList = BeanUtil.copyToList(paramTemplateFieldList,
                ParamTemplateFieldListVO.class);

        return paramTemplateFieldListVOList;
    }

    @Override
    public ParamTemplateFieldVO queryTemplateFieldDetail(Long fieldId) {
        if(fieldId == null){
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        ParamTemplateField paramTemplateField = paramTemplateFieldMapper.selectById(fieldId);
        if(paramTemplateField == null){
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        ParamTemplateFieldVO paramTemplateFieldVO = new ParamTemplateFieldVO();
        BeanUtil.copyProperties(paramTemplateField, paramTemplateFieldVO);
        return paramTemplateFieldVO;
    }

    @Override
    public void addTemplateField(ParamTemplateFieldSaveDTO paramTemplateFieldSaveDTO) {
        if (paramTemplateFieldSaveDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        String paramCode = paramTemplateFieldSaveDTO.getParamCode();
        String paramCn = paramTemplateFieldSaveDTO.getParamCn();
        String paramEn = paramTemplateFieldSaveDTO.getParamEn();

        QueryWrapper<ParamTemplateField> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
                .eq("param_code", paramCode)
                .or()
                .eq("param_cn", paramCn)
                .or()
                .eq("param_en", paramEn));
        long count = paramTemplateFieldMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "参数字段重复");
        }

        ParamTemplateField paramTemplateField = new ParamTemplateField();
        BeanUtil.copyProperties(paramTemplateFieldSaveDTO, paramTemplateField);
        paramTemplateFieldMapper.insert(paramTemplateField);
    }

    @Override
    public void updateTemplateField(ParamTemplateFieldUpdateDTO paramTemplateFieldUpdateDTO) {
        if (paramTemplateFieldUpdateDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        String paramCode = paramTemplateFieldUpdateDTO.getParamCode();
        String paramCn = paramTemplateFieldUpdateDTO.getParamCn();
        String paramEn = paramTemplateFieldUpdateDTO.getParamEn();
        QueryWrapper<ParamTemplateField> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
                .eq("param_code", paramCode)
                .or()
                .eq("param_cn", paramCn)
                .or()
                .eq("param_en", paramEn));
        queryWrapper.ne("template_id", paramTemplateFieldUpdateDTO.getTemplateId());

        long count = paramTemplateFieldMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_DUPLICATE, "参数字段已存在");
        }
        ParamTemplateField paramTemplateField = new ParamTemplateField();
        BeanUtil.copyProperties(paramTemplateFieldUpdateDTO, paramTemplateField);
        paramTemplateFieldMapper.update(queryWrapper);
    }

    @Override
    public void deleteTemplateField(Long fieldId) {
        if (fieldId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        QueryWrapper<ParamTemplateField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("field_id", fieldId);
        long count = paramTemplateFieldMapper.selectCount(null);
        if (count == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字段不存在");
        }
        paramTemplateFieldMapper.deleteById(fieldId);
    }



}
