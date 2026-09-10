package module.part.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.AviatorRuleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.part.dto.PartParamValueQueryDTO;
import module.part.dto.PartParamValueSaveDTO;
import module.part.dto.PartParamValueUpdateDTO;
import module.part.dto.SaveParamDTO;
import module.part.entity.PartInfo;
import module.part.entity.PartParamValue;
import module.part.mapper.PartInfoMapper;
import module.part.mapper.PartParamValueMapper;
import module.part.service.PartParamValueService;
import module.part.vo.PartParamFieldVO;
import module.part.vo.PartParamValueVO;
import module.part.vo.SaveParamResultVO;
import module.template.entity.ParamFieldCheckRule;
import module.template.entity.ParamTemplateField;
import module.template.mapper.ParamFieldCheckRuleMapper;
import module.template.mapper.ParamTemplateFieldMapper;

@RequiredArgsConstructor
@Service
@Slf4j
public class PartParamValueServiceImpl implements PartParamValueService {

    /** 必填类型：非必填 */
    private static final int REQUIRED_TYPE_NONE = 0;
    /** 必填类型：全局必填 */
    private static final int REQUIRED_TYPE_ALWAYS = 1;
    /** 必填类型：条件必填 */
    private static final int REQUIRED_TYPE_CONDITION = 2;

    private final PartParamValueMapper partParamValueMapper;
    private final PartInfoMapper partInfoMapper;
    private final ParamTemplateFieldMapper paramTemplateFieldMapper;
    private final ParamFieldCheckRuleMapper paramFieldCheckRuleMapper;

    @Override
    public PartParamValueVO queryPartParamValueDetail(Long paramValId) {
        if (paramValId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "配件参数值ID不能为空");
        }
        LambdaQueryWrapper<PartParamValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartParamValue::getParamValId, paramValId);
        PartParamValue partParamValue = partParamValueMapper.selectOne(queryWrapper);
        if (partParamValue == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件参数值不存在");
        }
        PartParamValueVO partParamValueVO = new PartParamValueVO();
        BeanUtil.copyProperties(partParamValue, partParamValueVO);
        return partParamValueVO;
    }

    @Override
    public List<PartParamFieldVO> listFieldVO(Long partId) {
        // ① 查配件，拿到 templateId
        PartInfo partInfo = getPartInfoOrThrow(partId);

        // 查这个模板的全部字段定义
        List<ParamTemplateField> fields = listTemplateFields(partInfo.getTemplateId());

        // 查这个配件已录入的全部值 → Map<paramCode, value>
        Map<String, Object> paramMap = buildParamMap(partId);

        // 逐个 buildVO，只留 showFlag=true 的
        List<PartParamFieldVO> result = new ArrayList<>();
        for (ParamTemplateField f : fields) {
            PartParamFieldVO vo = buildVO(f, paramMap);
            if (Boolean.TRUE.equals(vo.getShowFlag())) {
                result.add(vo);
            }
        }

        result.sort(Comparator.comparingInt(this::sortWeight));
        return result;
    }

    /**
     * 方案A核心：单条参数保存
     * <p>
     * 同一 partId + fieldId 只保留一条记录：
     * 有值 → upsert；四个值全空 → 删除。保存后自动清理条件不成立的脏数据。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaveParamResultVO saveSingleParam(Long partId, SaveParamDTO dto) {
        if (partId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "partId不能为空");
        }
        if (dto == null || dto.getFieldId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "fieldId不能为空");
        }

        PartInfo partInfo = getPartInfoOrThrow(partId);

        ParamTemplateField field = paramTemplateFieldMapper.selectById(dto.getFieldId());
        if (field == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "模板参数字段不存在");
        }
        // 字段必须属于该配件所用模板，防止跨模板写值
        if (!partInfo.getTemplateId().equals(field.getTemplateId())) {
            throw new BusinessException(ResultCode.PARAM_VALUE_INVALID, "该字段不属于当前配件的参数模板");
        }

        // 只校验当前字段自身的 CheckRule，不碰 requiredExpression
        validateFieldValue(field, dto);

        if (isBlankValue(dto)) {
            // 空值 → 清空该字段
            partParamValueMapper.deleteByPartIdAndFieldId(partId, field.getFieldId());
        } else {
            upsertValue(partId, field, dto);
        }

        // 清理脏数据：条件表达式变成 false，但仍然留着旧值
        List<PartParamFieldVO> cleared = clearHiddenButHasValue(partId, partInfo.getTemplateId());

        SaveParamResultVO result = new SaveParamResultVO();
        result.setFieldList(listFieldVO(partId));
        result.setClearedList(cleared);
        return result;
    }

    /**
     * 方案A核心：整套提交校验（纯校验，不写库）
     */
    @Override
    public void submitAll(Long partId) {
        PartInfo partInfo = getPartInfoOrThrow(partId);

        List<ParamTemplateField> fields = listTemplateFields(partInfo.getTemplateId());
        Map<String, Object> paramMap = buildParamMap(partId);

        List<String> errors = new ArrayList<>();
        for (ParamTemplateField f : fields) {
            Integer type = f.getRequiredType();
            if (type == null || type == REQUIRED_TYPE_NONE) {
                continue;
            }

            boolean hasValue = paramMap.get(f.getParamCode()) != null;

            if (type == REQUIRED_TYPE_ALWAYS) {
                if (!hasValue) {
                    errors.add("【" + f.getParamCn() + "】为必填项，请录入");
                }
            } else if (type == REQUIRED_TYPE_CONDITION) {
                // 条件必填：双向校验 —— 该填的没填、不该填的填了，都是错
                boolean cond = AviatorRuleUtil.isRequiredField(f.getRequiredExpression(), paramMap);
                if (cond && !hasValue) {
                    errors.add("【" + f.getParamCn() + "】在当前配置下为必填项，请录入");
                } else if (!cond && hasValue) {
                    errors.add("【" + f.getParamCn() + "】在当前配置下无需填写，请清空");
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_CHECK_ERROR, String.join("；", errors));
        }
    }

    @Override
    @Deprecated
    public void addPartParamValue(PartParamValueSaveDTO partParamValueSaveDTO) {
        if (partParamValueSaveDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "配件参数值保存DTO不能为空");
        }

        LambdaQueryWrapper<PartParamValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(PartParamValue::getPartId, partParamValueSaveDTO.getPartId())
                .eq(PartParamValue::getFieldId, partParamValueSaveDTO.getFieldId());

        long count = partParamValueMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "配件参数值已存在");
        }

        PartParamValue partParamValue = new PartParamValue();
        BeanUtil.copyProperties(partParamValueSaveDTO, partParamValue);
        int rows = partParamValueMapper.insert(partParamValue);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.ERROR, "新增配件参数值失败");
        }

    }

    @Override
    @Deprecated
    public void updatePartParamValue(PartParamValueUpdateDTO partParamValueUpdateDTO) {
        if (partParamValueUpdateDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "配件参数值更新DTO不能为空");
        }

        PartParamValue partParamValue = partParamValueMapper.selectById(partParamValueUpdateDTO.getParamValId());
        if (partParamValue == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件参数值不存在");
        }

        BeanUtil.copyProperties(partParamValueUpdateDTO, partParamValue);
        int rows = partParamValueMapper.updateById(partParamValue);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.ERROR, "更新配件参数值失败");
        }
    }

    @Override
    @Deprecated
    public void deletePartParamValue(Long paramValId) {
        if (paramValId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "配件参数值ID不能为空");
        }

        PartParamValue partParamValue = partParamValueMapper.selectById(paramValId);
        if (partParamValue == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件参数值不存在");
        }

        int rows = partParamValueMapper.deleteById(paramValId);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.ERROR, "删除配件参数值失败");
        }
    }

    // ==================== 私有方法 ====================

    private PartInfo getPartInfoOrThrow(Long partId) {
        if (partId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "partId不能为空");
        }
        PartInfo partInfo = partInfoMapper.selectById(partId);
        if (partInfo == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件不存在：" + partId);
        }
        if (partInfo.getTemplateId() == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件未绑定参数模板：" + partId);
        }
        return partInfo;
    }

    private List<ParamTemplateField> listTemplateFields(Long templateId) {
        LambdaQueryWrapper<ParamTemplateField> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParamTemplateField::getTemplateId, templateId);
        return paramTemplateFieldMapper.selectList(queryWrapper);
    }

    /**
     * 校验当前字段自身的 CheckRule（不跑 requiredExpression）
     */
    private void validateFieldValue(ParamTemplateField field, SaveParamDTO dto) {
        LambdaQueryWrapper<ParamFieldCheckRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParamFieldCheckRule::getFieldId, field.getFieldId())
                .eq(ParamFieldCheckRule::getStatus, 1);
        List<ParamFieldCheckRule> rules = paramFieldCheckRuleMapper.selectList(queryWrapper);
        if (rules.isEmpty()) {
            return;
        }

        // 组装数据：value 为约定变量名，paramCode 兼容"表达式直接引用字段编码"的写法
        Object value = dto.getNumValue() != null ? dto.getNumValue() : dto.getTextValue();
        Map<String, Object> data = new HashMap<>(4);
        data.put("value", value);
        if (StringUtils.hasText(field.getParamCode())) {
            data.put(field.getParamCode(), value);
        }

        List<ParamFieldCheckRule> failed = AviatorRuleUtil.executeAllFailedRule(rules, data);
        if (!failed.isEmpty()) {
            String msg = failed.stream()
                    .map(ParamFieldCheckRule::getErrorMsg)
                    .collect(Collectors.joining("；"));
            throw new BusinessException(ResultCode.PARAM_CHECK_ERROR, msg);
        }
    }

    /**
     * 按 partId + fieldId upsert 一条参数值
     */
    private void upsertValue(Long partId, ParamTemplateField field, SaveParamDTO dto) {
        LambdaQueryWrapper<PartParamValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartParamValue::getPartId, partId)
                .eq(PartParamValue::getFieldId, field.getFieldId());
        PartParamValue existing = partParamValueMapper.selectOne(queryWrapper);

        if (existing == null) {
            PartParamValue pv = new PartParamValue();
            pv.setPartId(partId);
            pv.setFieldId(field.getFieldId());
            pv.setNumValue(dto.getNumValue());
            pv.setTextValue(dto.getTextValue());
            pv.setNumMin(dto.getNumMin());
            pv.setNumMax(dto.getNumMax());
            partParamValueMapper.insert(pv);
        } else {
            existing.setNumValue(dto.getNumValue());
            existing.setTextValue(dto.getTextValue());
            existing.setNumMin(dto.getNumMin());
            existing.setNumMax(dto.getNumMax());
            partParamValueMapper.updateById(existing);
        }
    }

    /**
     * 清理脏数据：条件必填表达式不成立、但库里还留着值的字段
     *
     * @return 被清除的字段（保留旧值，供前端提示）
     */
    private List<PartParamFieldVO> clearHiddenButHasValue(Long partId, Long templateId) {
        List<ParamTemplateField> allFields = listTemplateFields(templateId);
        Map<String, Object> paramMap = buildParamMap(partId);

        List<PartParamFieldVO> cleared = new ArrayList<>();
        for (ParamTemplateField f : allFields) {
            // 只看条件必填字段
            if (!Integer.valueOf(REQUIRED_TYPE_CONDITION).equals(f.getRequiredType())) {
                continue;
            }

            boolean cond = AviatorRuleUtil.isRequiredField(f.getRequiredExpression(), paramMap);
            if (cond) {
                continue; // 条件成立，保留
            }

            Object val = paramMap.get(f.getParamCode());
            if (val == null) {
                continue; // 本来就没值
            }

            partParamValueMapper.deleteByPartIdAndFieldId(partId, f.getFieldId());

            // 保留旧值给前端做"已自动清除 XX"的提示
            PartParamFieldVO vo = buildVO(f, paramMap);
            vo.setParamValue(val);
            cleared.add(vo);

            log.info("自动清除脏数据：partId={}, fieldCode={}, oldValue={}",
                    partId, f.getParamCode(), val);
        }
        return cleared;
    }

    /**
     * 四个值全空视为"清空该字段"
     */
    private boolean isBlankValue(SaveParamDTO dto) {
        return dto.getNumValue() == null
                && dto.getNumMin() == null
                && dto.getNumMax() == null
                && !StringUtils.hasText(dto.getTextValue());
    }

    private PartParamFieldVO buildVO(ParamTemplateField paramTemplateField,
            Map<String, Object> paramMap) {
        PartParamFieldVO vo = new PartParamFieldVO();

        // ① 拷贝静态定义
        vo.setFieldId(paramTemplateField.getFieldId());
        vo.setParamCode(paramTemplateField.getParamCode());
        vo.setParamCn(paramTemplateField.getParamCn());
        vo.setParamUnit(paramTemplateField.getUnit());
        vo.setDataType(paramTemplateField.getDataType());
        vo.setRequiredType(paramTemplateField.getRequiredType());
        vo.setRequiredExpression(paramTemplateField.getRequiredExpression());
        vo.setSort(paramTemplateField.getSort());

        // ② 从 map 里取值
        Object val = paramMap.get(paramTemplateField.getParamCode());
        vo.setParamValue(val);
        vo.setHasValue(val != null);

        // ③ 算 showFlag / requiredFlag（null 归一化成 0）
        int type = paramTemplateField.getRequiredType() == null
                ? REQUIRED_TYPE_NONE
                : paramTemplateField.getRequiredType();

        if (type == REQUIRED_TYPE_NONE) {
            vo.setShowFlag(true);
            vo.setRequiredFlag(false);
        } else if (type == REQUIRED_TYPE_ALWAYS) {
            vo.setShowFlag(true);
            vo.setRequiredFlag(true);
        } else if (type == REQUIRED_TYPE_CONDITION) {
            String expr = paramTemplateField.getRequiredExpression();
            boolean cond = AviatorRuleUtil.isRequiredField(expr, paramMap);
            if (!cond) {
                log.debug("字段[{}]条件不成立，隐藏。expr={}, paramKeys={}",
                        paramTemplateField.getParamCode(), expr, paramMap.keySet());
            }
            vo.setShowFlag(cond);
            vo.setRequiredFlag(cond);
        } else {
            // 兜底：未知类型当非必填处理，避免脏配置炸掉整个列表
            log.warn("未知 requiredType={}, fieldId={}",
                    paramTemplateField.getRequiredType(), paramTemplateField.getFieldId());
            vo.setShowFlag(true);
            vo.setRequiredFlag(false);
        }

        // ④ 算状态标签
        if (!Boolean.TRUE.equals(vo.getShowFlag())) {
            vo.setStatusTag("HIDDEN");
        } else if (Boolean.TRUE.equals(vo.getHasValue())) {
            vo.setStatusTag("FILLED");
        } else if (Boolean.TRUE.equals(vo.getRequiredFlag())) {
            vo.setStatusTag("REQUIRED_UNFILLED");
        } else {
            vo.setStatusTag("OPTIONAL");
        }

        return vo;
    }

    private Map<String, Object> buildParamMap(Long partId) {
        List<PartParamValueQueryDTO> list = partParamValueMapper.selectValueListByPartId(partId);
        Map<String, Object> paramMap = new HashMap<>(list.size());

        for (PartParamValueQueryDTO dto : list) {
            Object value = extractValue(dto);
            if (value != null) { // 空值不放进 map，让 Aviator 视为"变量缺失"
                paramMap.put(dto.getParamCode(), value);
            }
        }
        return paramMap;
    }

    /**
     * 根据 data_type 从 num_value / text_value 里取值
     */
    private Object extractValue(PartParamValueQueryDTO dto) {
        String type = dto.getDataType();
        if (type == null) {
            return null;
        }

        switch (type) {
            case "number":
                // 阶段1：区间参数不参与条件表达式
                // 区间 [min,max] 无法直接和标量比较，混进 paramMap 会让表达式结果不可预期，
                // 需要区间比较时再单独引入自定义函数。
                if (dto.getNumMin() != null || dto.getNumMax() != null) {
                    return null;
                }
                return dto.getNumValue();

            case "bool":
                String text = dto.getTextValue();
                if (text == null) {
                    return null;
                }
                // 存的是 "true"/"false" 字符串，Aviator 里要布尔
                return "true".equalsIgnoreCase(text) || "1".equals(text);

            case "enum":
            case "text":
            case "date":
                return dto.getTextValue();

            default:
                log.warn("未知 dataType: {}, paramCode={}", type, dto.getParamCode());
                return null;
        }
    }

    /**
     * 排序权重：数字越小越靠前
     * 1-全局必填 → 0
     * 2-条件必填 → 1
     * 0-非必填 → 2
     * null/其他 → 3
     */
    private int sortWeight(PartParamFieldVO vo) {
        Integer t = vo.getRequiredType();
        if (t == null)
            return 3;
        switch (t) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 0:
                return 2;
            default:
                return 3;
        }
    }
}
