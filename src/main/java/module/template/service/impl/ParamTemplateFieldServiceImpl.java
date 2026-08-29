package module.template.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import common.aviator.AviatorRuleUtil;
import common.enums.ParamDataTypeEnum;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.system.service.SysDictTypeService;
import module.system.vo.DictOptionVO;
import module.template.dto.ParamFieldCheckRuleSaveDTO;
import module.template.dto.ParamFieldCheckRuleUpdateDTO;
import module.template.dto.ParamTemplateFieldSaveDTO;
import module.template.dto.ParamTemplateFieldUpdateDTO;
import module.template.entity.ParamFieldCheckRule;
import module.template.entity.ParamTemplateField;
import module.template.mapper.ParamFieldCheckRuleMapper;
import module.template.mapper.ParamTemplateFieldMapper;
import module.template.service.ParamTemplateFieldService;
import module.template.vo.CurrentTemplateFieldListVO;
import module.template.vo.ParamFieldCheckRuleVO;
import module.template.vo.ParamTemplateFieldListVO;
import module.template.vo.ParamTemplateFieldVO;

@RequiredArgsConstructor
@Service
public class ParamTemplateFieldServiceImpl implements ParamTemplateFieldService {

    private final ParamTemplateFieldMapper paramTemplateFieldMapper;
    private final ParamFieldCheckRuleMapper paramFieldCheckRuleMapper;
    private final SysDictTypeService sysDictTypeService;

    @Override
    public List<ParamTemplateFieldListVO> queryTemplateFieldList(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        QueryWrapper<ParamTemplateField> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("template_id", templateId);
        List<ParamTemplateField> paramTemplateFieldList = paramTemplateFieldMapper.selectList(queryWrapper);

        List<ParamTemplateFieldListVO> paramTemplateFieldListVOList = BeanUtil.copyToList(paramTemplateFieldList,
                ParamTemplateFieldListVO.class);

        return paramTemplateFieldListVOList;
    }

    @Override
    public ParamTemplateFieldVO queryTemplateFieldDetail(Long fieldId) {
        if (fieldId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        ParamTemplateField paramTemplateField = paramTemplateFieldMapper.selectById(fieldId);
        if (paramTemplateField == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        ParamTemplateFieldVO paramTemplateFieldVO = new ParamTemplateFieldVO();
        BeanUtil.copyProperties(paramTemplateField, paramTemplateFieldVO);

        LambdaQueryWrapper<ParamFieldCheckRule> ruleWrapper = Wrappers.lambdaQuery();
        ruleWrapper
                .eq(ParamFieldCheckRule::getFieldId, paramTemplateField.getFieldId())
                .orderByAsc(ParamFieldCheckRule::getSort);
        List<ParamFieldCheckRule> paramFieldCheckRuleList = paramFieldCheckRuleMapper.selectList(ruleWrapper);
        List<ParamFieldCheckRuleVO> checkRuleVOS = BeanUtil.copyToList(paramFieldCheckRuleList,
                ParamFieldCheckRuleVO.class);
        paramTemplateFieldVO.setCheckRuleList(checkRuleVOS);

        return paramTemplateFieldVO;
    }

    @Override
    public List<CurrentTemplateFieldListVO> queryCurrentTemplateFieldList(Long templateId) {
        if (templateId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        QueryWrapper<ParamTemplateField> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("template_id", templateId);
        List<ParamTemplateField> paramTemplateFieldList = paramTemplateFieldMapper.selectList(queryWrapper);

        List<CurrentTemplateFieldListVO> voList = paramTemplateFieldList.stream()
                .map(field -> {
                    CurrentTemplateFieldListVO vo = new CurrentTemplateFieldListVO();
                    BeanUtil.copyProperties(field, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        return voList;
    }

    @Override
    public List<DictOptionVO> queryEnumParamValues(Long fieldId) {
        if (fieldId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }
        ParamTemplateField paramTemplateField = paramTemplateFieldMapper.selectById(fieldId);
        if (paramTemplateField == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        if (!ParamDataTypeEnum.ENUM.getType().equals(paramTemplateField.getDataType())) {
            return new ArrayList<>();
        }
        return sysDictTypeService.getEnableDictItemByDictCode(paramTemplateField.getRelDictCode());
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

        List<ParamFieldCheckRuleSaveDTO> checkRuleVoList = paramTemplateFieldSaveDTO.getCheckRuleList();
        if (CollectionUtil.isNotEmpty(checkRuleVoList)) {
            List<ParamFieldCheckRule> paramFieldCheckRules = BeanUtil.copyToList(checkRuleVoList,
                    ParamFieldCheckRule.class);

            AviatorRuleUtil.validateExprSyntax(AviatorRuleUtil.buildCheckExpr(paramFieldCheckRules));        
            
            paramFieldCheckRuleMapper.insert(paramFieldCheckRules);
        }
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

        List<ParamFieldCheckRuleUpdateDTO> checkRuleVoList = paramTemplateFieldUpdateDTO.getCheckRuleList();
        if (CollectionUtil.isNotEmpty(checkRuleVoList)) {
            List<ParamFieldCheckRule> paramFieldCheckRules = BeanUtil.copyToList(checkRuleVoList,
                    ParamFieldCheckRule.class);
            AviatorRuleUtil.validateExprSyntax(AviatorRuleUtil.buildCheckExpr(paramFieldCheckRules));
            paramFieldCheckRuleMapper.updateById(paramFieldCheckRules);
        }
    }

    @Override
    public void deleteTemplateField(Long fieldId) {
        if (fieldId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        QueryWrapper<ParamTemplateField> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("field_id", fieldId);
        long count = paramTemplateFieldMapper.selectCount(null);
        if (count == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字段不存在");
        }
        paramTemplateFieldMapper.deleteById(fieldId);
    }

}
