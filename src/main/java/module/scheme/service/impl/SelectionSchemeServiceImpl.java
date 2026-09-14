package module.scheme.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.AviatorRuleUtil;
import common.until.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.part.dto.PartParamValueQueryDTO;
import module.part.entity.PartInfo;
import module.part.mapper.PartInfoMapper;
import module.part.mapper.PartParamValueMapper;
import module.scheme.dto.SchemePartQuantityDTO;
import module.scheme.dto.SchemePartQueryDTO;
import module.scheme.dto.SchemeQueryDTO;
import module.scheme.dto.SchemeSaveSelectionDTO;
import module.scheme.dto.SchemeUpdateDTO;
import module.scheme.entity.SchemeConflictLog;
import module.scheme.entity.SchemePart;
import module.scheme.entity.SelectionScheme;
import module.scheme.mapper.SchemeConflictLogMapper;
import module.scheme.mapper.SchemePartMapper;
import module.scheme.mapper.SelectionSchemeMapper;
import module.scheme.service.SelectionSchemeService;
import module.scheme.support.SchemePartVoFiller;
import module.scheme.vo.SchemeBriefVO;
import module.scheme.vo.SchemeDetailVO;
import module.scheme.vo.SchemePartVO;
import module.scheme.vo.SchemePartValidateVO;
import module.scheme.vo.SchemeSummaryVO;
import module.scheme.vo.SchemeValidateResultVO;
import module.scheme.vo.ValidateFailItemVO;
import module.template.entity.ParamFieldCheckRule;
import module.template.entity.ParamTemplateField;
import module.template.mapper.ParamFieldCheckRuleMapper;
import module.template.mapper.ParamTemplateFieldMapper;

/**
 * 选型方案业务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SelectionSchemeServiceImpl implements SelectionSchemeService {

    private final SelectionSchemeMapper selectionSchemeMapper;

    private final SchemePartMapper schemePartMapper;

    private final SchemeConflictLogMapper schemeConflictLogMapper;

    private final PartInfoMapper partInfoMapper;

    private final PartParamValueMapper partParamValueMapper;

    private final ParamTemplateFieldMapper paramTemplateFieldMapper;

    private final ParamFieldCheckRuleMapper paramFieldCheckRuleMapper;

    private final ObjectMapper objectMapper;

    /** 配件参与选型的状态值：仅正式发布的配件可以进入方案 */
    private static final int PUBLISHING_STATUS_PUBLISHED = 1;

    /** 正式 BOM 标记 */
    private static final int IS_SELECT_BOM = 1;

    /**
     * 保存选配结果为方案
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveSelection(SchemeSaveSelectionDTO dto) {
        checkNotBlank(dto, "数据为空");

        // 方案名唯一
        checkSchemeNameDuplicate(dto.getSchemeName(), null);

        // 按分类互斥 + 配件存在性/发布状态校验
        validateItems(dto.getItems());

        SelectionScheme scheme = new SelectionScheme();
        scheme.setSchemeName(dto.getSchemeName());
        scheme.setUserId(SecurityUtils.getCurrentUserId());
        scheme.setWholeCarReq(writeWholeCarReq(dto.getWholeCarReq()));
        scheme.setRemark(dto.getRemark());
        int rows = selectionSchemeMapper.insert(scheme);
        if (rows == 0) {
            throw new BusinessException(ResultCode.ERROR, "保存方案失败");
        }

        for (SchemeSaveSelectionDTO.Item item : dto.getItems()) {
            insertSchemePart(scheme.getSchemeId(), item.getPartId(), item.getQuantity());
        }
        return scheme.getSchemeId();
    }

    /**
     * 分页查询方案列表
     */
    @Override
    public Page<SchemeBriefVO> querySchemeList(SchemeQueryDTO dto) {
        SchemeQueryDTO query = dto == null ? new SchemeQueryDTO() : dto;
        Page<SchemeBriefVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return selectionSchemeMapper.selectSchemePage(page, query);
    }

    /**
     * 查询方案详情
     */
    @Override
    public SchemeDetailVO querySchemeDetail(Long schemeId) {
        SelectionScheme scheme = getSchemeOrThrow(schemeId);
        SchemeDetailVO vo = selectionSchemeMapper.selectSchemeDetail(scheme.getSchemeId());
        if (vo == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "方案不存在");
        }
        // whole_car_req 在库里是 TEXT，聚合查询不取它（VO 里是 Map，直接映射会类型不匹配），
        // 这里用前面已查出的实体里的原始 JSON 反序列化后填入
        vo.setWholeCarReq(readWholeCarReq(scheme.getWholeCarReq()));
        return vo;
    }

    /**
     * 分页查询方案配件明细
     */
    @Override
    public Page<SchemePartVO> querySchemePartPage(Long schemeId, SchemePartQueryDTO dto) {
        getSchemeOrThrow(schemeId);
        SchemePartQueryDTO query = dto == null ? new SchemePartQueryDTO() : dto;
        Page<SchemePartVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SchemePartVO> result = schemePartMapper.selectSchemePartPage(page, schemeId, query);
        result.getRecords().forEach(SchemePartVoFiller::fill);
        return result;
    }

    /**
     * 修改方案基本信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScheme(SchemeUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        if (dto.getSchemeId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "schemeId 不能为空");
        }
        getSchemeOrThrow(dto.getSchemeId());
        checkSchemeNameDuplicate(dto.getSchemeName(), dto.getSchemeId());

        SelectionScheme update = new SelectionScheme();
        update.setSchemeId(dto.getSchemeId());
        update.setSchemeName(dto.getSchemeName());
        update.setRemark(dto.getRemark());
        // wholeCarReq 传 null 表示不修改；要清空请传空对象 {}
        if (dto.getWholeCarReq() != null) {
            update.setWholeCarReq(writeWholeCarReq(dto.getWholeCarReq()));
        }
        int rows = selectionSchemeMapper.updateById(update);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "方案不存在");
        }
    }

    /**
     * 删除方案（级联删除明细与冲突日志）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteScheme(Long schemeId) {
        getSchemeOrThrow(schemeId);
        // 冲突日志依附于方案，方案没了日志也就没有意义，一并清理
        LambdaQueryWrapper<SchemeConflictLog> conflictWrapper = new LambdaQueryWrapper<>();
        conflictWrapper.eq(SchemeConflictLog::getSchemeId, schemeId);
        schemeConflictLogMapper.delete(conflictWrapper);

        LambdaQueryWrapper<SchemePart> partWrapper = new LambdaQueryWrapper<>();
        partWrapper.eq(SchemePart::getSchemeId, schemeId);
        schemePartMapper.delete(partWrapper);

        int rows = selectionSchemeMapper.deleteById(schemeId);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "方案不存在");
        }
    }

    /**
     * 批量修改明细数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SchemeSummaryVO updatePartQuantity(Long schemeId, SchemePartQuantityDTO dto) {
        getSchemeOrThrow(schemeId);
        if (dto == null || dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "至少提交一条明细");
        }
        // items 内不允许重复提交同一条明细，否则更新结果取决于顺序
        Set<Long> seen = new HashSet<>();
        for (SchemePartQuantityDTO.Item item : dto.getItems()) {
            if (!seen.add(item.getSchemePartId())) {
                throw new BusinessException(ResultCode.DATA_DUPLICATE, "items 中存在重复的 schemePartId");
            }
        }
        Map<Long, SchemePart> owned = loadOwnedParts(schemeId, seen);

        for (SchemePartQuantityDTO.Item item : dto.getItems()) {
            SchemePart part = owned.get(item.getSchemePartId());
            if (part == null) {
                throw new BusinessException(ResultCode.DATA_NOT_EXIST, "明细不存在或不属于该方案");
            }
            SchemePart update = new SchemePart();
            update.setSchemePartId(item.getSchemePartId());
            update.setQuantity(item.getQuantity());
            schemePartMapper.updateById(update);
        }
        return querySummary(schemeId);
    }

    /**
     * 删除方案中的配件明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SchemeSummaryVO deleteSchemePart(Long schemeId, Long schemePartId) {
        getSchemeOrThrow(schemeId);
        SchemePart part = schemePartMapper.selectById(schemePartId);
        // 越权校验：明细必须属于路径上的方案，避免删掉别的方案的行
        if (part == null || !schemeId.equals(part.getSchemeId())) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "明细不存在或不属于该方案");
        }
        schemePartMapper.deleteById(schemePartId);
        return querySummary(schemeId);
    }

    /**
     * 方案参数合法性校验
     * <p>
     * 逐条执行校验规则且不中断，收集全部失败项一并返回。
     */
    @Override
    public SchemeValidateResultVO validateScheme(Long schemeId) {
        getSchemeOrThrow(schemeId);

        LambdaQueryWrapper<SchemePart> partWrapper = new LambdaQueryWrapper<>();
        partWrapper.eq(SchemePart::getSchemeId, schemeId);
        List<SchemePart> schemeParts = schemePartMapper.selectList(partWrapper);

        List<SchemePartValidateVO> results = new ArrayList<>();
        int ruleCount = 0;
        int failCount = 0;

        if (!schemeParts.isEmpty()) {
            // 一次性把配件信息、模板字段、校验规则都取出来，避免逐条查库
            Map<Long, PartInfo> partMap = loadParts(schemeParts.stream()
                    .map(SchemePart::getPartId).collect(Collectors.toSet()));
            Map<Long, List<ParamTemplateField>> fieldMap = loadFieldsByTemplate(partMap.values());
            Map<Long, List<ParamFieldCheckRule>> ruleMap = loadRulesByField(fieldMap.values());
            // paramCode -> fieldId 的映射按模板分组，避免不同模板间参数编码重名串号
            Map<Long, Map<String, Long>> codeToFieldMap = buildCodeToFieldMap(fieldMap);

            for (SchemePart schemePart : schemeParts) {
                PartInfo part = partMap.get(schemePart.getPartId());
                if (part == null) {
                    // 配件被删了：跳过校验，不算失败项
                    continue;
                }
                List<ParamTemplateField> fields = fieldMap.getOrDefault(part.getTemplateId(), List.of());
                Map<Long, Object> valueMap = loadParamValues(schemePart.getPartId(),
                        codeToFieldMap.getOrDefault(part.getTemplateId(), Map.of()));

                List<ValidateFailItemVO> failItems = new ArrayList<>();
                for (ParamTemplateField field : fields) {
                    List<ParamFieldCheckRule> rules = ruleMap.getOrDefault(field.getFieldId(), List.of());
                    if (rules.isEmpty()) {
                        continue;
                    }
                    ruleCount += rules.size();

                    Object value = valueMap.get(field.getFieldId());
                    // 区间参数不参与表达式校验（与 module.part 既有语义一致）
                    if (value == null) {
                        continue;
                    }
                    List<ParamFieldCheckRule> failed =
                            AviatorRuleUtil.executeAllFailedRule(rules, buildAviatorEnv(field, value));
                    failCount += failed.size();
                    for (ParamFieldCheckRule rule : failed) {
                        failItems.add(toFailItem(field, rule, value));
                    }
                }

                if (!failItems.isEmpty()) {
                    SchemePartValidateVO partResult = new SchemePartValidateVO();
                    partResult.setSchemePartId(schemePart.getSchemePartId());
                    partResult.setPartId(schemePart.getPartId());
                    partResult.setPartName(SchemePartVoFiller.buildPartName(null, part.getModel()));
                    partResult.setFailItems(failItems);
                    results.add(partResult);
                }
            }
        }

        SchemeValidateResultVO vo = new SchemeValidateResultVO();
        vo.setPass(failCount == 0);
        vo.setRuleCount(ruleCount);
        vo.setFailCount(failCount);
        vo.setResults(results);
        return vo;
    }

    // ==================== 私有方法 ====================

    /**
     * 校验入参非空
     */
    private void checkNotBlank(SchemeSaveSelectionDTO dto, String message) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, message);
        }
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "至少选择一个配件");
        }
    }

    /**
     * 校验选配明细：分类互斥、配件存在、配件已发布、分类归属正确
     */
    private void validateItems(List<SchemeSaveSelectionDTO.Item> items) {
        // 同一三级分类下只能选一款配件
        Set<Long> catIds = new HashSet<>();
        for (SchemeSaveSelectionDTO.Item item : items) {
            if (!catIds.add(item.getCatId())) {
                throw new BusinessException(ResultCode.PARAM_DUPLICATE,
                        "分类【" + item.getCatId() + "】下只能选择一款配件");
            }
        }

        // 一次性批量取配件，避免循环单查
        Set<Long> partIds = items.stream()
                .map(SchemeSaveSelectionDTO.Item::getPartId)
                .collect(Collectors.toSet());
        Map<Long, PartInfo> partMap = loadParts(partIds);

        for (SchemeSaveSelectionDTO.Item item : items) {
            PartInfo part = partMap.get(item.getPartId());
            if (part == null) {
                throw new BusinessException(ResultCode.DATA_NOT_EXIST,
                        "配件不存在：[" + item.getPartId() + "]");
            }
            if (!Objects.equals(part.getCatId(), item.getCatId())) {
                throw new BusinessException(ResultCode.PARAM_VALUE_INVALID,
                        "配件【" + part.getPartCode() + "】不属于所选分类");
            }
            if (!Integer.valueOf(PUBLISHING_STATUS_PUBLISHED).equals(part.getPublishingStatus())) {
                throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                        "配件【" + part.getPartCode() + "】未发布，不能参与选型");
            }
        }
    }

    /**
     * 批量查询配件
     */
    private Map<Long, PartInfo> loadParts(Collection<Long> partIds) {
        if (partIds == null || partIds.isEmpty()) {
            return new HashMap<>();
        }
        return partInfoMapper.selectByIds(partIds).stream()
                .collect(Collectors.toMap(PartInfo::getPartId, p -> p, (a, b) -> a));
    }

    /**
     * 写入一条方案明细
     */
    private void insertSchemePart(Long schemeId, Long partId, Integer quantity) {
        SchemePart schemePart = new SchemePart();
        schemePart.setSchemeId(schemeId);
        schemePart.setPartId(partId);
        schemePart.setQuantity(quantity);
        // 手动选配直接计入正式BOM，匹配度暂固定为 0（智能匹配为二期能力）
        schemePart.setMatchScore(BigDecimal.ZERO);
        schemePart.setIsSelectBom(IS_SELECT_BOM);
        schemePartMapper.insert(schemePart);
    }

    /**
     * 校验方案存在并返回
     */
    private SelectionScheme getSchemeOrThrow(Long schemeId) {
        if (schemeId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "schemeId 不能为空");
        }
        SelectionScheme scheme = selectionSchemeMapper.selectById(schemeId);
        if (scheme == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "方案不存在");
        }
        return scheme;
    }

    /**
     * 方案名唯一性校验
     *
     * @param excludeSchemeId 修改场景下需排除的自身ID，新增时传 null
     */
    private void checkSchemeNameDuplicate(String schemeName, Long excludeSchemeId) {
        LambdaQueryWrapper<SelectionScheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SelectionScheme::getSchemeName, schemeName);
        if (excludeSchemeId != null) {
            wrapper.ne(SelectionScheme::getSchemeId, excludeSchemeId);
        }
        if (selectionSchemeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "方案名【" + schemeName + "】已存在");
        }
    }

    /**
     * 查询属于指定方案的明细，用于越权校验
     */
    private Map<Long, SchemePart> loadOwnedParts(Long schemeId, Collection<Long> schemePartIds) {
        if (schemePartIds.isEmpty()) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<SchemePart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchemePart::getSchemeId, schemeId)
                .in(SchemePart::getSchemePartId, schemePartIds);
        return schemePartMapper.selectList(wrapper).stream()
                .collect(Collectors.toMap(SchemePart::getSchemePartId, p -> p, (a, b) -> a));
    }

    /**
     * 重新统计方案汇总，供改数量/删明细后直接返回
     */
    private SchemeSummaryVO querySummary(Long schemeId) {
        SchemeDetailVO detail = selectionSchemeMapper.selectSchemeDetail(schemeId);
        SchemeSummaryVO vo = new SchemeSummaryVO();
        vo.setPartCount(detail == null ? 0 : detail.getPartCount());
        vo.setTotalPrice(detail == null ? BigDecimal.ZERO : detail.getTotalPrice());
        return vo;
    }

    /**
     * 按模板ID批量取参数字段
     */
    private Map<Long, List<ParamTemplateField>> loadFieldsByTemplate(Collection<PartInfo> parts) {
        Set<Long> templateIds = parts.stream()
                .map(PartInfo::getTemplateId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (templateIds.isEmpty()) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<ParamTemplateField> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ParamTemplateField::getTemplateId, templateIds)
                .orderByAsc(ParamTemplateField::getSort);
        return paramTemplateFieldMapper.selectList(wrapper).stream()
                .collect(Collectors.groupingBy(ParamTemplateField::getTemplateId));
    }

    /**
     * 按字段ID批量取启用的校验规则
     */
    private Map<Long, List<ParamFieldCheckRule>> loadRulesByField(
            Collection<List<ParamTemplateField>> fieldGroups) {
        Set<Long> fieldIds = fieldGroups.stream()
                .flatMap(List::stream)
                .map(ParamTemplateField::getFieldId)
                .collect(Collectors.toSet());
        if (fieldIds.isEmpty()) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<ParamFieldCheckRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ParamFieldCheckRule::getFieldId, fieldIds)
                .eq(ParamFieldCheckRule::getStatus, 1)
                .orderByAsc(ParamFieldCheckRule::getSort);
        return paramFieldCheckRuleMapper.selectList(wrapper).stream()
                .collect(Collectors.groupingBy(ParamFieldCheckRule::getFieldId));
    }

    /**
     * 取某配件已录入的参数值，key 为 fieldId
     * <p>
     * selectValueListByPartId 返回的是 paramCode，需要经调用方预先构建好的
     * paramCode → fieldId 映射换算成 fieldId。区间参数（num_min/num_max）不参与
     * 表达式校验，由 resolveParamValue 过滤掉。
     *
     * @param partId       配件主键ID
     * @param codeToField  该配件所属模板的 paramCode → fieldId 映射
     */
    private Map<Long, Object> loadParamValues(Long partId, Map<String, Long> codeToField) {
        Map<Long, Object> map = new HashMap<>();
        if (partId == null || codeToField.isEmpty()) {
            return map;
        }
        List<PartParamValueQueryDTO> values = partParamValueMapper.selectValueListByPartId(partId);
        if (values == null || values.isEmpty()) {
            return map;
        }
        for (PartParamValueQueryDTO value : values) {
            Long fieldId = codeToField.get(value.getParamCode());
            if (fieldId == null) {
                continue;
            }
            Object resolved = resolveParamValue(value);
            if (resolved != null) {
                map.put(fieldId, resolved);
            }
        }
        return map;
    }

    /**
     * 构建「模板ID → (paramCode → fieldId)」映射，供换算参数值归属的字段使用
     */
    private Map<Long, Map<String, Long>> buildCodeToFieldMap(
            Map<Long, List<ParamTemplateField>> fieldMap) {
        Map<Long, Map<String, Long>> result = new HashMap<>();
        for (Map.Entry<Long, List<ParamTemplateField>> entry : fieldMap.entrySet()) {
            Map<String, Long> codeToField = new HashMap<>();
            for (ParamTemplateField field : entry.getValue()) {
                if (StringUtils.hasText(field.getParamCode())) {
                    codeToField.putIfAbsent(field.getParamCode(), field.getFieldId());
                }
            }
            result.put(entry.getKey(), codeToField);
        }
        return result;
    }

    /**
     * 按数据类型归一化参数值：number 取数值，其余取文本
     */
    private Object resolveParamValue(PartParamValueQueryDTO value) {
        if ("number".equals(value.getDataType())) {
            // 区间参数（只录了 min/max）不参与表达式
            return value.getNumValue();
        }
        return value.getTextValue();
    }

    /**
     * 构造 Aviator 上下文
     * <p>
     * 同时绑定 {@code param} 与参数的 paramCode 两个 key，指向同一个值：
     * 接口文档中的校验表达式用 {@code param}，而仓库既有调用方只绑 paramCode。
     * 两个都绑可让两种写法都生效，且不必改动既有模块。
     */
    private Map<String, Object> buildAviatorEnv(ParamTemplateField field, Object value) {
        Map<String, Object> env = new HashMap<>();
        env.put("param", value);
        if (StringUtils.hasText(field.getParamCode())) {
            env.put(field.getParamCode(), value);
        }
        return env;
    }

    /**
     * 组装一条失败项
     */
    private ValidateFailItemVO toFailItem(ParamTemplateField field, ParamFieldCheckRule rule, Object value) {
        ValidateFailItemVO item = new ValidateFailItemVO();
        item.setFieldId(field.getFieldId());
        item.setParamCn(field.getParamCn());
        item.setCheckRuleId(rule.getCheckRuleId());
        item.setRuleName(rule.getRuleName());
        item.setCheckExpr(rule.getCheckExpr());
        item.setActualValue(value == null ? null : String.valueOf(value));
        item.setErrorMsg(rule.getErrorMsg());
        return item;
    }

    /**
     * Map 序列化为 JSON 字符串存库；不传时存空对象，因为该列 NOT NULL
     */
    private String writeWholeCarReq(Map<String, Object> wholeCarReq) {
        try {
            return objectMapper.writeValueAsString(wholeCarReq == null ? Map.of() : wholeCarReq);
        } catch (Exception e) {
            log.warn("整车需求序列化失败", e);
            throw new BusinessException(ResultCode.PARAM_FORMAT_ERROR, "整车需求格式错误");
        }
    }

    /**
     * JSON 字符串反序列化为 Map
     */
    private Map<String, Object> readWholeCarReq(String json) {
        if (!StringUtils.hasText(json)) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.warn("整车需求反序列化失败，按空对象处理: {}", json, e);
            return new HashMap<>();
        }
    }
}
