package module.compat.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import common.enums.RuleSeverity;
import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.AviatorRuleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.category.entity.PartCategory;
import module.category.mapper.PartCategoryMapper;
import module.compat.mapper.ConflictRuleMapper;
import module.compat.service.ConflictCheckService;
import module.compat.vo.ConflictItemVO;
import module.compat.vo.ConflictResultVO;
import module.compat.vo.ConflictRuleVO;
import module.part.dto.PartParamValueQueryDTO;
import module.part.entity.PartInfo;
import module.part.mapper.PartInfoMapper;
import module.part.mapper.PartParamValueMapper;
import module.scheme.entity.SchemeConflictLog;
import module.scheme.entity.SchemePart;
import module.scheme.entity.SelectionScheme;
import module.scheme.mapper.SchemeConflictLogMapper;
import module.scheme.mapper.SchemePartMapper;
import module.scheme.mapper.SelectionSchemeMapper;
import module.scheme.support.SchemePartVoFiller;

/**
 * 方案跨配件兼容性冲突检测业务实现类
 * <p>
 * 与参数校验共用同一个 Aviator 引擎（{@link AviatorRuleUtil}），
 * 差别只在求值上下文：参数校验一次装一个配件的参数，
 * 冲突检测一次装两个配件，分别绑成 {@code a} / {@code b} 两个变量。
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ConflictCheckServiceImpl implements ConflictCheckService {

    private final SelectionSchemeMapper selectionSchemeMapper;

    private final SchemePartMapper schemePartMapper;

    private final SchemeConflictLogMapper schemeConflictLogMapper;

    private final PartInfoMapper partInfoMapper;

    private final PartParamValueMapper partParamValueMapper;

    private final PartCategoryMapper partCategoryMapper;

    private final ConflictRuleMapper conflictRuleMapper;

    /** 二级子系统层级：规则挂在这一层 */
    private static final int SUBSYSTEM_LEVEL = 2;

    /**
     * 对指定方案执行跨配件兼容性检测
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConflictResultVO checkScheme(Long schemeId) {
        if (schemeId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "schemeId 不能为空");
        }
        SelectionScheme scheme = selectionSchemeMapper.selectById(schemeId);
        if (scheme == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "方案不存在");
        }

        LambdaQueryWrapper<SchemePart> partWrapper = new LambdaQueryWrapper<>();
        partWrapper.eq(SchemePart::getSchemeId, schemeId);
        List<SchemePart> schemeParts = schemePartMapper.selectList(partWrapper);

        List<ConflictItemVO> conflicts = new ArrayList<>();
        int ruleCount = 0;
        int undeterminedCount = 0;

        if (!schemeParts.isEmpty()) {
            // 1. 批量取配件信息，并把每个配件归到它的二级子系统
            Map<Long, PartInfo> partMap = loadParts(schemeParts.stream()
                    .map(SchemePart::getPartId).collect(Collectors.toSet()));
            Map<Long, Long> partToSubsystem = resolveSubsystems(partMap.values());

            // 方案里实际出现的子系统集合
            Set<Long> subsystemIds = new HashSet<>(partToSubsystem.values());
            if (!subsystemIds.isEmpty()) {
                // 2. 捞出子系统组合命中的启用规则
                List<ConflictRuleVO> rules =
                        conflictRuleMapper.selectEnabledByCatPairs(subsystemIds, subsystemIds);
                ruleCount = rules.size();

                // 3. 逐条规则 × 逐对「有序」配件
                Map<Long, Map<String, Object>> paramCache = new HashMap<>();
                for (ConflictRuleVO rule : rules) {
                    for (SchemePart a : schemeParts) {
                        for (SchemePart b : schemeParts) {
                            if (Objects.equals(a.getSchemePartId(), b.getSchemePartId())) {
                                continue;
                            }
                            if (!belongsTo(partToSubsystem, a, rule.getCatAId())
                                    || !belongsTo(partToSubsystem, b, rule.getCatBId())) {
                                continue;
                            }
                            PartInfo partA = partMap.get(a.getPartId());
                            PartInfo partB = partMap.get(b.getPartId());
                            if (partA == null || partB == null) {
                                continue;
                            }

                            Map<String, Object> mapA = paramCache.computeIfAbsent(
                                    partA.getPartId(), this::loadParamMap);
                            Map<String, Object> mapB = paramCache.computeIfAbsent(
                                    partB.getPartId(), this::loadParamMap);

                            // 关键：两侧参数齐全才能判定。
                            // 缺参数时 Aviator 不会抛异常，而是静默返回 false，
                            // 若不做前置判断就会把一个「没法比」误报成「冲突」。
                            boolean hasA = mapA.containsKey(rule.getParamCodeA());
                            boolean hasB = mapB.containsKey(rule.getParamCodeB());
                            if (!hasA || !hasB) {
                                undeterminedCount++;
                                continue;
                            }

                            Map<String, Object> env = new HashMap<>();
                            env.put("a", mapA);
                            env.put("b", mapB);
                            boolean compatible;
                            try {
                                compatible = AviatorRuleUtil.execute(rule.getCheckExpr(), env);
                            } catch (Exception e) {
                                // 表达式本身有问题（例如规则配置错误），按未判定处理，
                                // 不能当成冲突，否则规则写错会伪装成配件不兼容
                                log.warn("冲突规则表达式执行失败，按未判定处理: ruleId={}, expr={}",
                                        rule.getRuleId(), rule.getCheckExpr(), e);
                                undeterminedCount++;
                                continue;
                            }
                            if (compatible) {
                                continue;
                            }
                            conflicts.add(toConflictItem(rule, partA, partB,
                                    mapA.get(rule.getParamCodeA()), mapB.get(rule.getParamCodeB())));
                        }
                    }
                }
            }
        }

        int hardCount = 0;
        int softCount = 0;
        for (ConflictItemVO item : conflicts) {
            if (RuleSeverity.isHard(item.getSeverity())) {
                hardCount++;
            } else {
                softCount++;
            }
        }

        // 覆盖式落库：先清空该方案的旧日志，避免重复调用不断累积
        LambdaQueryWrapper<SchemeConflictLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(SchemeConflictLog::getSchemeId, schemeId);
        schemeConflictLogMapper.delete(logWrapper);
        for (ConflictItemVO item : conflicts) {
            SchemeConflictLog entity = new SchemeConflictLog();
            entity.setSchemeId(schemeId);
            entity.setPartAId(item.getPartAId());
            entity.setPartBId(item.getPartBId());
            entity.setConflictParam(item.getConflictParam());
            entity.setConflictDesc(item.getConflictDesc());
            entity.setSolveSuggest(item.getSolveSuggest());
            schemeConflictLogMapper.insert(entity);
        }

        ConflictResultVO vo = new ConflictResultVO();
        vo.setSchemeId(schemeId);
        vo.setCompatible(hardCount == 0);
        vo.setHardConflictCount(hardCount);
        vo.setSoftConflictCount(softCount);
        vo.setUndeterminedCount(undeterminedCount);
        vo.setRuleCount(ruleCount);
        vo.setConflicts(conflicts);
        return vo;
    }

    // ==================== 私有方法 ====================

    /**
     * 该明细所属的二级子系统是否等于期望值
     */
    private boolean belongsTo(Map<Long, Long> partToSubsystem, SchemePart schemePart, Long expectedCatId) {
        Long actual = partToSubsystem.get(schemePart.getPartId());
        return actual != null && actual.equals(expectedCatId);
    }

    /**
     * 批量取配件信息
     */
    private Map<Long, PartInfo> loadParts(Collection<Long> partIds) {
        if (partIds == null || partIds.isEmpty()) {
            return new HashMap<>();
        }
        return partInfoMapper.selectByIds(partIds).stream()
                .collect(Collectors.toMap(PartInfo::getPartId, p -> p, (a, b) -> a));
    }

    /**
     * 把配件映射到它所属的二级子系统
     * <p>
     * part_info.cat_id 是三级配件类别，需要跳一次父节点到二级子系统。
     * 规则挂二级，所以同一个二级下的所有三级类别都共享规则。
     */
    private Map<Long, Long> resolveSubsystems(Collection<PartInfo> parts) {
        Set<Long> leafCatIds = parts.stream()
                .map(PartInfo::getCatId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (leafCatIds.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, PartCategory> categoryMap = partCategoryMapper.selectByIds(leafCatIds).stream()
                .collect(Collectors.toMap(PartCategory::getCatId, c -> c, (a, b) -> a));

        // 父节点可能不在 leafCatIds 里，单独再查一次
        Set<Long> parentIds = categoryMap.values().stream()
                .map(PartCategory::getParentCatId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, PartCategory> parentMap = parentIds.isEmpty() ? Map.of()
                : partCategoryMapper.selectByIds(parentIds).stream()
                        .collect(Collectors.toMap(PartCategory::getCatId, c -> c, (a, b) -> a));

        Map<Long, Long> result = new HashMap<>();
        for (PartInfo part : parts) {
            PartCategory leaf = categoryMap.get(part.getCatId());
            if (leaf == null) {
                continue;
            }
            // 三级类别 -> 二级子系统
            PartCategory parent = parentMap.get(leaf.getParentCatId());
            Long subsystemId = parent != null && Integer.valueOf(SUBSYSTEM_LEVEL).equals(parent.getLevel())
                    ? parent.getCatId()
                    : null;
            if (subsystemId != null) {
                result.put(part.getPartId(), subsystemId);
            }
        }
        return result;
    }

    /**
     * 取配件已录入的参数映射，key 为 paramCode
     * <p>区间参数（只录了 min/max）不参与比较，与参数校验的口径保持一致。
     */
    private Map<String, Object> loadParamMap(Long partId) {
        Map<String, Object> map = new LinkedHashMap<>();
        List<PartParamValueQueryDTO> values = partParamValueMapper.selectValueListByPartId(partId);
        if (values == null) {
            return map;
        }
        for (PartParamValueQueryDTO value : values) {
            if (!StringUtils.hasText(value.getParamCode())) {
                continue;
            }
            Object resolved = "number".equals(value.getDataType())
                    ? value.getNumValue()
                    : value.getTextValue();
            if (resolved != null) {
                map.put(value.getParamCode(), resolved);
            }
        }
        return map;
    }

    /**
     * 把参与比较的取值转成展示字符串
     * <p>
     * num_value 是 decimal(18,4)，直接 toString 会得到「48.0000」这种带零尾巴的结果，
     * 展示给用户时不合适，故去掉多余的零。
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
     * 组装一条冲突明细
     */
    private ConflictItemVO toConflictItem(ConflictRuleVO rule, PartInfo partA, PartInfo partB,
            Object valueA, Object valueB) {
        ConflictItemVO item = new ConflictItemVO();
        item.setRuleId(rule.getRuleId());
        item.setRuleName(rule.getRuleName());

        item.setPartAId(partA.getPartId());
        item.setPartACode(partA.getPartCode());
        item.setPartAName(SchemePartVoFiller.buildPartName(rule.getCatAName(), partA.getModel()));
        item.setParamCodeA(rule.getParamCodeA());
        item.setValueA(formatValue(valueA));

        item.setPartBId(partB.getPartId());
        item.setPartBCode(partB.getPartCode());
        item.setPartBName(SchemePartVoFiller.buildPartName(rule.getCatBName(), partB.getModel()));
        item.setParamCodeB(rule.getParamCodeB());
        item.setValueB(formatValue(valueB));

        item.setConflictParam(rule.getParamCodeA() + " ↔ " + rule.getParamCodeB());
        item.setConflictDesc(rule.getErrorMsg());
        item.setSolveSuggest(rule.getSolveSuggest());
        item.setSeverity(rule.getSeverity());
        RuleSeverity severity = RuleSeverity.getByCode(rule.getSeverity());
        item.setSeverityName(severity == null ? null : severity.getDesc());
        return item;
    }
}
