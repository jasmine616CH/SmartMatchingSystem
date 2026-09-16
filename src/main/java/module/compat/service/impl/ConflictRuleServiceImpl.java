package module.compat.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import common.enums.RuleSeverity;
import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.AviatorRuleUtil;
import lombok.RequiredArgsConstructor;
import module.category.entity.PartCategory;
import module.category.mapper.PartCategoryMapper;
import module.compat.dto.ConflictRuleQueryDTO;
import module.compat.dto.ConflictRuleSaveDTO;
import module.compat.dto.ConflictRuleUpdateDTO;
import module.compat.entity.ConflictRule;
import module.compat.mapper.ConflictRuleMapper;
import module.compat.service.ConflictRuleService;
import module.compat.vo.ConflictRuleVO;
import module.compat.vo.ExprValidateVO;
import module.compat.vo.ParamCodeOptionVO;
import module.compat.vo.SubsystemVO;

/**
 * 配件兼容冲突规则业务实现类
 */
@RequiredArgsConstructor
@Service
public class ConflictRuleServiceImpl implements ConflictRuleService {

    private final ConflictRuleMapper conflictRuleMapper;

    private final PartCategoryMapper partCategoryMapper;

    /** 冲突规则只能挂在二级子系统上（part_category.level = 2） */
    private static final int REQUIRED_CATEGORY_LEVEL = 2;

    /** 默认启用 */
    private static final int STATUS_ENABLED = 1;

    /**
     * 分页查询冲突规则
     */
    @Override
    public Page<ConflictRuleVO> queryRuleList(ConflictRuleQueryDTO dto) {
        ConflictRuleQueryDTO query = dto == null ? new ConflictRuleQueryDTO() : dto;
        Page<ConflictRuleVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<ConflictRuleVO> result = conflictRuleMapper.selectRulePage(page, query);
        result.getRecords().forEach(this::fillSeverityName);
        return result;
    }

    /**
     * 查询冲突规则详情
     */
    @Override
    public ConflictRuleVO queryRuleDetail(Long ruleId) {
        if (ruleId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "ruleId 不能为空");
        }
        ConflictRuleVO vo = conflictRuleMapper.selectRuleById(ruleId);
        if (vo == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "冲突规则不存在");
        }
        fillSeverityName(vo);
        return vo;
    }

    /**
     * 新增冲突规则
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRule(ConflictRuleSaveDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        validateRule(dto.getCatAId(), dto.getCatBId(), dto.getCheckExpr());

        ConflictRule entity = BeanUtil.toBean(dto, ConflictRule.class);
        entity.setSeverity(normalizeSeverity(dto.getSeverity()));
        entity.setStatus(dto.getStatus() == null ? STATUS_ENABLED : dto.getStatus());
        entity.setSort(dto.getSort() == null ? 0 : dto.getSort());

        int rows = conflictRuleMapper.insert(entity);
        if (rows == 0) {
            throw new BusinessException(ResultCode.ERROR, "新增冲突规则失败");
        }
    }

    /**
     * 修改冲突规则
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRule(ConflictRuleUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        if (dto.getRuleId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "ruleId 不能为空");
        }
        if (conflictRuleMapper.selectById(dto.getRuleId()) == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "冲突规则不存在");
        }
        validateRule(dto.getCatAId(), dto.getCatBId(), dto.getCheckExpr());

        ConflictRule entity = BeanUtil.toBean(dto, ConflictRule.class);
        entity.setSeverity(normalizeSeverity(dto.getSeverity()));

        int rows = conflictRuleMapper.updateById(entity);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "冲突规则不存在");
        }
    }

    /**
     * 删除冲突规则
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRule(Long ruleId) {
        if (ruleId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "ruleId 不能为空");
        }
        if (conflictRuleMapper.selectById(ruleId) == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "冲突规则不存在");
        }
        conflictRuleMapper.deleteById(ruleId);
    }

    // ==================== 规则编辑器的辅助接口 ====================

    /**
     * 查询全部二级子系统
     */
    @Override
    public List<SubsystemVO> querySubsystems() {
        return conflictRuleMapper.selectSubsystems();
    }

    /**
     * 查询某二级子系统下的参数编码候选
     */
    @Override
    public List<ParamCodeOptionVO> queryParamCodes(Long catId) {
        if (catId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "catId 不能为空");
        }
        return conflictRuleMapper.selectParamCodesBySubsystem(catId);
    }

    /**
     * 校验表达式语法（不落库）
     * <p>
     * 复用参数校验那套语法校验，报错信息原样返回给编辑器展示；
     * 这里把 BusinessException 转成结果对象，避免编辑器每次输错都当成请求失败。
     */
    @Override
    public ExprValidateVO validateExpr(String checkExpr) {
        ExprValidateVO vo = new ExprValidateVO();
        if (checkExpr == null || checkExpr.trim().isEmpty()) {
            vo.setValid(false);
            vo.setMessage("表达式不能为空");
            return vo;
        }
        try {
            AviatorRuleUtil.validateExprSyntax(checkExpr);
            vo.setValid(true);
        } catch (BusinessException e) {
            vo.setValid(false);
            vo.setMessage(e.getMessage());
        }
        return vo;
    }

    // ==================== 私有方法 ====================

    /**
     * 校验规则两侧的子系统与表达式
     */
    private void validateRule(Long catAId, Long catBId, String checkExpr) {
        checkSubsystem(catAId, "catAId");
        checkSubsystem(catBId, "catBId");
        // 表达式语法沿用参数校验那套，同一个引擎同一个报错口径
        AviatorRuleUtil.validateExprSyntax(checkExpr);
    }

    /**
     * 校验分类存在且为二级子系统
     * <p>规则挂二级子系统，其下所有三级配件类别自动适用；挂到三级会把规则写死到具体型号上。
     */
    private void checkSubsystem(Long catId, String field) {
        if (catId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, field + " 不能为空");
        }
        PartCategory category = partCategoryMapper.selectById(catId);
        if (category == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件分类不存在：" + catId);
        }
        if (!Integer.valueOf(REQUIRED_CATEGORY_LEVEL).equals(category.getLevel())) {
            throw new BusinessException(ResultCode.PARAM_VALUE_INVALID,
                    "冲突规则只能挂在二级子系统上，" + field + " 的层级为 " + category.getLevel());
        }
    }

    /**
     * 约束强度归一化：不传按硬性处理，与库中默认值一致
     */
    private Integer normalizeSeverity(Integer severity) {
        return severity == null ? RuleSeverity.HARD.getCode() : severity;
    }

    private void fillSeverityName(ConflictRuleVO vo) {
        RuleSeverity severity = RuleSeverity.getByCode(vo.getSeverity());
        vo.setSeverityName(severity == null ? null : severity.getDesc());
    }
}
