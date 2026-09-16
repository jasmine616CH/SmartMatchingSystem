package module.scheme.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import common.enums.ParamMatchStatus;
import common.enums.RuleSeverity;
import common.result.ResultCode;
import common.until.AviatorRuleUtil;
import common.until.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.part.dto.PartParamValueQueryDTO;
import module.part.entity.PartInfo;
import module.category.entity.PartCategory;
import module.category.mapper.PartCategoryMapper;
import module.part.mapper.PartInfoMapper;
import module.part.mapper.PartParamValueMapper;
import module.scheme.dto.SchemeCompareQueryDTO;
import module.scheme.dto.SchemeCopyDTO;
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
import module.price.mapper.PartSupplierPriceMapper;
import module.price.vo.PartUnitPriceVO;
import module.scheme.support.SchemePartVoFiller;
import module.scheme.vo.CompareCellVO;
import module.scheme.vo.ComparePartVO;
import module.scheme.vo.CompareRowVO;
import module.scheme.vo.MissingParamVO;
import module.scheme.vo.SchemeCompareVO;
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

    private final PartCategoryMapper partCategoryMapper;

    private final PartSupplierPriceMapper partSupplierPriceMapper;

    private final ObjectMapper objectMapper;

    /** 配件参与选型的状态值：仅正式发布的配件可以进入方案 */
    private static final int PUBLISHING_STATUS_PUBLISHED = 2;

    /** 正式 BOM 标记 */
    private static final int IS_SELECT_BOM = 1;

    /** 并排比较一次最多参与的候选件数 */
    private static final int MAX_CANDIDATES = 10;

    /** 必填类型：非必填 */
    private static final int REQUIRED_TYPE_NONE = 0;

    /** 必填类型：全局必填 */
    private static final int REQUIRED_TYPE_ALWAYS = 1;

    /** 必填类型：条件必填 */
    private static final int REQUIRED_TYPE_CONDITION = 2;

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
     * 每个参数落到四种判定态之一：满足 / 临界 / 不满足 / 缺失。
     * <ul>
     *   <li>硬性约束（severity=0）未通过 → 不满足，计入 failCount，直接影响 pass</li>
     *   <li>偏好条件（severity=1）未通过 → 临界，只计入 softFailCount，不影响 pass</li>
     *   <li>必填参数未录入 → 缺失，计入 missingCount，直接影响 pass</li>
     * </ul>
     * 逐条执行不中断，收集全部问题一并返回。
     * <p>
     * 同时把算出的参数匹配度写回 {@code scheme_part.match_score}，供明细分页按匹配度排序。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SchemeValidateResultVO validateScheme(Long schemeId) {
        getSchemeOrThrow(schemeId);

        LambdaQueryWrapper<SchemePart> partWrapper = new LambdaQueryWrapper<>();
        partWrapper.eq(SchemePart::getSchemeId, schemeId);
        List<SchemePart> schemeParts = schemePartMapper.selectList(partWrapper);

        List<SchemePartValidateVO> results = new ArrayList<>();
        int ruleCount = 0;
        int failCount = 0;
        int softFailCount = 0;
        int missingCount = 0;

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
                PartParamContext paramContext = loadParamContext(schemePart.getPartId(),
                        codeToFieldMap.getOrDefault(part.getTemplateId(), Map.of()));

                List<ValidateFailItemVO> failItems = new ArrayList<>();
                List<ValidateFailItemVO> warnItems = new ArrayList<>();
                List<MissingParamVO> missingItems = new ArrayList<>();
                // 偏好条件的通过数，用于算匹配度
                int preferenceTotal = 0;
                int preferencePassed = 0;

                for (ParamTemplateField field : fields) {
                    List<ParamFieldCheckRule> rules = ruleMap.getOrDefault(field.getFieldId(), List.of());
                    Object value = paramContext.byFieldId().get(field.getFieldId());

                    // 缺失：必填（含条件必填成立）但没录入值
                    if (value == null) {
                        ruleCount += rules.size();
                        if (isRequired(field, paramContext.byCode())) {
                            missingCount++;
                            missingItems.add(toMissingItem(field, rules.size()));
                        }
                        continue;
                    }
                    if (rules.isEmpty()) {
                        continue;
                    }
                    ruleCount += rules.size();

                    // 按约束强度拆开跑：硬性不过 = 不满足，偏好不过 = 临界
                    List<ParamFieldCheckRule> hardRules = rules.stream()
                            .filter(r -> RuleSeverity.isHard(r.getSeverity()))
                            .toList();
                    List<ParamFieldCheckRule> softRules = rules.stream()
                            .filter(r -> !RuleSeverity.isHard(r.getSeverity()))
                            .toList();

                    Map<String, Object> env = buildAviatorEnv(field, value);
                    for (ParamFieldCheckRule rule : AviatorRuleUtil.executeAllFailedRule(hardRules, env)) {
                        failItems.add(toFailItem(field, rule, value));
                    }
                    List<ParamFieldCheckRule> softFailed =
                            AviatorRuleUtil.executeAllFailedRule(softRules, env);
                    for (ParamFieldCheckRule rule : softFailed) {
                        warnItems.add(toFailItem(field, rule, value));
                    }
                    preferenceTotal += softRules.size();
                    preferencePassed += softRules.size() - softFailed.size();
                }

                failCount += failItems.size();
                softFailCount += warnItems.size();

                // 匹配度 = 通过的偏好条件 / 偏好条件总数；没有偏好条件则不写库（保持 0 = 未计算）
                BigDecimal matchScore = null;
                if (preferenceTotal > 0) {
                    matchScore = BigDecimal.valueOf(preferencePassed)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(preferenceTotal), 2, RoundingMode.HALF_UP);
                    SchemePart update = new SchemePart();
                    update.setSchemePartId(schemePart.getSchemePartId());
                    update.setMatchScore(matchScore);
                    schemePartMapper.updateById(update);
                }

                if (!failItems.isEmpty() || !warnItems.isEmpty() || !missingItems.isEmpty()) {
                    SchemePartValidateVO partResult = new SchemePartValidateVO();
                    partResult.setSchemePartId(schemePart.getSchemePartId());
                    partResult.setPartId(schemePart.getPartId());
                    partResult.setPartCode(part.getPartCode());
                    partResult.setPartName(SchemePartVoFiller.buildPartName(null, part.getModel()));
                    partResult.setFailItems(failItems);
                    partResult.setWarnItems(warnItems);
                    partResult.setMissingItems(missingItems);
                    partResult.setMatchScore(matchScore);
                    results.add(partResult);
                }
            }
        }

        SchemeValidateResultVO vo = new SchemeValidateResultVO();
        // 硬性约束全过且必填齐全才算通过；偏好条件不满足不影响 pass
        vo.setPass(failCount == 0 && missingCount == 0);
        vo.setRuleCount(ruleCount);
        vo.setFailCount(failCount);
        vo.setSoftFailCount(softFailCount);
        vo.setMissingCount(missingCount);
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
     * 一个配件已录入的参数值，同时提供两种索引
     * <ul>
     *   <li>byFieldId —— 按模板字段ID索引，用于把值喂给校验表达式</li>
     *   <li>byCode —— 按 paramCode 索引，用于给条件必填表达式求值（表达式里写的是参数编码）</li>
     * </ul>
     */
    private record PartParamContext(Map<Long, Object> byFieldId, Map<String, Object> byCode) {
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
    private PartParamContext loadParamContext(Long partId, Map<String, Long> codeToField) {
        Map<Long, Object> byFieldId = new HashMap<>();
        Map<String, Object> byCode = new HashMap<>();
        if (partId == null || codeToField.isEmpty()) {
            return new PartParamContext(byFieldId, byCode);
        }
        List<PartParamValueQueryDTO> values = partParamValueMapper.selectValueListByPartId(partId);
        if (values == null || values.isEmpty()) {
            return new PartParamContext(byFieldId, byCode);
        }
        for (PartParamValueQueryDTO value : values) {
            Long fieldId = codeToField.get(value.getParamCode());
            if (fieldId == null) {
                continue;
            }
            Object resolved = resolveParamValue(value);
            if (resolved != null) {
                byFieldId.put(fieldId, resolved);
                byCode.put(value.getParamCode(), resolved);
            }
        }
        return new PartParamContext(byFieldId, byCode);
    }

    /**
     * 判断参数是否为「当前必须填写」
     * <p>
     * 与 module.part 的既有语义保持一致：required_type 为空或 0 视为非必填；
     * 1 为全局必填；2 为条件必填，条件表达式为真时才算必填。
     *
     * @param field    模板参数字段
     * @param paramMap 该配件已录入的参数（key 为 paramCode），条件表达式据此求值
     */
    private boolean isRequired(ParamTemplateField field, Map<String, Object> paramMap) {
        Integer type = field.getRequiredType();
        if (type == null || REQUIRED_TYPE_NONE == type) {
            return false;
        }
        if (REQUIRED_TYPE_ALWAYS == type) {
            return true;
        }
        if (REQUIRED_TYPE_CONDITION == type) {
            return AviatorRuleUtil.isRequiredField(field.getRequiredExpression(), paramMap);
        }
        // 未知取值一律按非必填处理，避免脏数据把整个方案判成缺失
        return false;
    }

    /**
     * 组装一条缺失项
     */
    private MissingParamVO toMissingItem(ParamTemplateField field, int ruleCount) {
        MissingParamVO item = new MissingParamVO();
        item.setFieldId(field.getFieldId());
        item.setParamCode(field.getParamCode());
        item.setParamCn(field.getParamCn());
        item.setUnit(field.getUnit());
        item.setRequiredType(field.getRequiredType());
        item.setRequiredTypeName(REQUIRED_TYPE_ALWAYS == field.getRequiredType() ? "全局必填" : "条件必填");
        item.setRuleCount(ruleCount);
        return item;
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

    /**
     * 候选件并排比较
     * <p>
     * 判定口径与 {@link #validateScheme(Long)} 完全一致（同一套规则、同一套 Aviator），
     * 区别只是输出形状：校验接口按配件聚合，这里展开成「参数 × 配件」矩阵，
     * 便于前端把同一参数在不同候选件上的表现横向摆开对比。
     */
    @Override
    public SchemeCompareVO compareSchemes(SchemeCompareQueryDTO dto) {
        if (dto == null || dto.getCatId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "catId 不能为空");
        }
        PartCategory category = partCategoryMapper.selectById(dto.getCatId());
        if (category == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件分类不存在");
        }

        List<PartInfo> parts = loadCompareParts(dto);
        SchemeCompareVO result = new SchemeCompareVO();
        result.setCatId(dto.getCatId());
        result.setCatName(category.getCatName());
        if (parts.isEmpty()) {
            result.setParts(List.of());
            result.setRows(List.of());
            return result;
        }

        // 参与比较的配件必须同属一个模板，否则参数行对不齐，比较没有意义
        Set<Long> templateIds = parts.stream()
                .map(PartInfo::getTemplateId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (templateIds.size() > 1) {
            throw new BusinessException(ResultCode.PARAM_VALUE_INVALID,
                    "所选配件使用了不同的参数模板，无法并排比较");
        }

        Long templateId = templateIds.isEmpty() ? null : templateIds.iterator().next();
        Map<Long, List<ParamTemplateField>> fieldMap = templateId == null
                ? Map.of() : loadFieldsByTemplate(parts);
        List<ParamTemplateField> fields = fieldMap.getOrDefault(templateId, List.of());
        Map<Long, List<ParamFieldCheckRule>> ruleMap = loadRulesByField(List.of(fields));
        Map<String, Long> codeToField = templateId == null ? Map.of()
                : buildCodeToFieldMap(Map.of(templateId, fields)).getOrDefault(templateId, Map.of());

        // 列头：配件基本信息 + 解析后的单价
        List<Long> partIds = parts.stream().map(PartInfo::getPartId).toList();
        Map<Long, PartUnitPriceVO> priceMap = partIds.isEmpty() ? Map.of()
                : partSupplierPriceMapper.selectUnitPriceByPartIds(partIds).stream()
                        .collect(Collectors.toMap(PartUnitPriceVO::getPartId, p -> p, (a, b) -> a));

        List<ComparePartVO> partVOs = new ArrayList<>();
        List<Map<Long, Object>> valueMaps = new ArrayList<>();
        for (PartInfo part : parts) {
            ComparePartVO pv = new ComparePartVO();
            pv.setPartId(part.getPartId());
            pv.setPartCode(part.getPartCode());
            pv.setPartName(SchemePartVoFiller.buildPartName(category.getCatName(), part.getModel()));
            pv.setBrand(part.getBrand());
            pv.setModel(part.getModel());
            // 金额固定人民币，与方案明细保持一致
            pv.setCurrency(SchemePartVoFiller.CURRENCY_CNY);
            PartUnitPriceVO price = priceMap.get(part.getPartId());
            if (price != null && price.getPriceValue() != null) {
                pv.setHasPrice(true);
                pv.setUnitPrice(price.getPriceValue());
            } else {
                pv.setHasPrice(false);
            }
            pv.setViolatedCount(0);
            pv.setWarningCount(0);
            pv.setMissingCount(0);
            partVOs.add(pv);
            valueMaps.add(loadParamContext(part.getPartId(), codeToField).byFieldId());
        }

        // 参数行：逐参数逐配件判定
        List<CompareRowVO> rows = new ArrayList<>();
        for (ParamTemplateField field : fields) {
            CompareRowVO row = new CompareRowVO();
            row.setFieldId(field.getFieldId());
            row.setParamCode(field.getParamCode());
            row.setParamCn(field.getParamCn());
            row.setUnit(field.getUnit());
            row.setDataType(field.getDataType());
            row.setRequiredType(field.getRequiredType());

            List<ParamFieldCheckRule> rules = ruleMap.getOrDefault(field.getFieldId(), List.of());
            List<ParamFieldCheckRule> hardRules = rules.stream()
                    .filter(r -> RuleSeverity.isHard(r.getSeverity())).toList();
            List<ParamFieldCheckRule> softRules = rules.stream()
                    .filter(r -> !RuleSeverity.isHard(r.getSeverity())).toList();

            List<CompareCellVO> cells = new ArrayList<>();
            for (int i = 0; i < parts.size(); i++) {
                cells.add(buildCell(parts.get(i).getPartId(), field,
                        hardRules, softRules, valueMaps.get(i).get(field.getFieldId())));
            }
            row.setCells(cells);
            rows.add(row);
        }
        result.setParts(partVOs);
        result.setRows(rows);

        // 汇总每列的判定计数
        Map<Long, ComparePartVO> partIndex = partVOs.stream()
                .collect(Collectors.toMap(ComparePartVO::getPartId, p -> p, (a, b) -> a));
        for (CompareRowVO row : rows) {
            for (CompareCellVO cell : row.getCells()) {
                ComparePartVO target = partIndex.get(cell.getPartId());
                if (target == null) {
                    continue;
                }
                if (ParamMatchStatus.VIOLATED.getCode().equals(cell.getStatus())) {
                    target.setViolatedCount(target.getViolatedCount() + 1);
                } else if (ParamMatchStatus.WARNING.getCode().equals(cell.getStatus())) {
                    target.setWarningCount(target.getWarningCount() + 1);
                } else if (ParamMatchStatus.MISSING.getCode().equals(cell.getStatus())) {
                    target.setMissingCount(target.getMissingCount() + 1);
                }
            }
        }
        return result;
    }

    /**
     * 复制方案（历史方案复用）
     * <p>明细整体复制，原方案不受影响。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyScheme(Long schemeId, SchemeCopyDTO dto) {
        SelectionScheme source = getSchemeOrThrow(schemeId);
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        checkSchemeNameDuplicate(dto.getSchemeName(), null);

        SelectionScheme target = new SelectionScheme();
        target.setSchemeName(dto.getSchemeName());
        target.setUserId(SecurityUtils.getCurrentUserId());
        // 整车需求就是筛选条件，一并复制才是「复用」的意义
        target.setWholeCarReq(source.getWholeCarReq());
        target.setRemark(StringUtils.hasText(dto.getRemark()) ? dto.getRemark() : source.getRemark());
        if (selectionSchemeMapper.insert(target) == 0) {
            throw new BusinessException(ResultCode.ERROR, "复制方案失败");
        }

        LambdaQueryWrapper<SchemePart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchemePart::getSchemeId, schemeId).orderByAsc(SchemePart::getSchemePartId);
        for (SchemePart src : schemePartMapper.selectList(wrapper)) {
            SchemePart copy = new SchemePart();
            copy.setSchemeId(target.getSchemeId());
            copy.setPartId(src.getPartId());
            copy.setQuantity(src.getQuantity());
            copy.setMatchScore(src.getMatchScore());
            copy.setIsSelectBom(src.getIsSelectBom());
            schemePartMapper.insert(copy);
        }
        return target.getSchemeId();
    }

    /**
     * 构造一个判定格子
     */
    private CompareCellVO buildCell(Long partId, ParamTemplateField field,
            List<ParamFieldCheckRule> hardRules, List<ParamFieldCheckRule> softRules, Object value) {
        CompareCellVO cell = new CompareCellVO();
        cell.setPartId(partId);
        cell.setMessages(new ArrayList<>());

        if (value == null) {
            cell.setValue(null);
            setStatus(cell, ParamMatchStatus.MISSING);
            return cell;
        }
        cell.setValue(formatValue(value));

        Map<String, Object> env = buildAviatorEnv(field, value);
        List<ParamFieldCheckRule> hardFailed = hardRules.isEmpty()
                ? List.of() : AviatorRuleUtil.executeAllFailedRule(hardRules, env);
        List<ParamFieldCheckRule> softFailed = softRules.isEmpty()
                ? List.of() : AviatorRuleUtil.executeAllFailedRule(softRules, env);

        for (ParamFieldCheckRule rule : hardFailed) {
            cell.getMessages().add(rule.getErrorMsg());
        }
        for (ParamFieldCheckRule rule : softFailed) {
            cell.getMessages().add(rule.getErrorMsg());
        }
        if (!hardFailed.isEmpty()) {
            setStatus(cell, ParamMatchStatus.VIOLATED);
        } else if (!softFailed.isEmpty()) {
            setStatus(cell, ParamMatchStatus.WARNING);
        } else {
            setStatus(cell, ParamMatchStatus.SATISFIED);
        }
        return cell;
    }

    private void setStatus(CompareCellVO cell, ParamMatchStatus status) {
        cell.setStatus(status.getCode());
        cell.setStatusName(status.getDesc());
    }

    /**
     * 把参数取值转成展示字符串
     * <p>num_value 是 decimal(18,4)，直接 toString 会得到「48.0000」，去掉多余的零。
     */
    private String formatValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.stripTrailingZeros().toPlainString();
        }
        return String.valueOf(value);
    }

    /**
     * 取参与比较的配件
     * <p>显式传入 partIds 时保留传入顺序（列序即前端想要的顺序）；
     * 未传时取该分类下全部已发布配件，最多 {@code MAX_CANDIDATES} 个。
     */
    private List<PartInfo> loadCompareParts(SchemeCompareQueryDTO dto) {
        if (dto.getPartIds() != null && !dto.getPartIds().isEmpty()) {
            if (dto.getPartIds().size() > MAX_CANDIDATES) {
                throw new BusinessException(ResultCode.PARAM_RANGE_ERROR,
                        "一次最多比较 " + MAX_CANDIDATES + " 个配件");
            }
            Map<Long, PartInfo> found = partInfoMapper.selectByIds(dto.getPartIds()).stream()
                    .collect(Collectors.toMap(PartInfo::getPartId, p -> p, (a, b) -> a));
            List<PartInfo> ordered = new ArrayList<>();
            for (Long id : dto.getPartIds()) {
                PartInfo part = found.get(id);
                if (part == null) {
                    throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件不存在：[" + id + "]");
                }
                if (!dto.getCatId().equals(part.getCatId())) {
                    throw new BusinessException(ResultCode.PARAM_VALUE_INVALID,
                            "配件【" + part.getPartCode() + "】不属于所选分类");
                }
                ordered.add(part);
            }
            return ordered;
        }
        LambdaQueryWrapper<PartInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartInfo::getCatId, dto.getCatId())
                .eq(PartInfo::getPublishingStatus, PUBLISHING_STATUS_PUBLISHED)
                .orderByAsc(PartInfo::getPartId)
                .last("LIMIT " + MAX_CANDIDATES);
        return partInfoMapper.selectList(wrapper);
    }
}
