package module.compat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

import module.compat.dto.ConflictRuleQueryDTO;
import module.compat.dto.ConflictRuleSaveDTO;
import module.compat.dto.ConflictRuleUpdateDTO;
import module.compat.vo.ConflictRuleVO;
import module.compat.vo.ExprValidateVO;
import module.compat.vo.ParamCodeOptionVO;
import module.compat.vo.SubsystemVO;

/**
 * 配件兼容冲突规则业务接口
 */
public interface ConflictRuleService {

    /**
     * 分页查询冲突规则
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    Page<ConflictRuleVO> queryRuleList(ConflictRuleQueryDTO dto);

    /**
     * 查询冲突规则详情
     *
     * @param ruleId 规则主键ID
     * @return 规则详情
     */
    ConflictRuleVO queryRuleDetail(Long ruleId);

    /**
     * 新增冲突规则
     *
     * @param dto 规则信息
     */
    void addRule(ConflictRuleSaveDTO dto);

    /**
     * 修改冲突规则
     *
     * @param dto 规则信息
     */
    void updateRule(ConflictRuleUpdateDTO dto);

    /**
     * 删除冲突规则
     *
     * @param ruleId 规则主键ID
     */
    void deleteRule(Long ruleId);

    // ==================== 规则编辑器的辅助接口 ====================

    /**
     * 查询全部二级子系统（规则只能挂在这一层）
     *
     * @return 二级子系统列表
     */
    List<SubsystemVO> querySubsystems();

    /**
     * 查询某二级子系统下的参数编码候选
     * <p>供表达式编辑器做自动补全。
     *
     * @param catId 二级子系统ID
     * @return 参数编码候选
     */
    List<ParamCodeOptionVO> queryParamCodes(Long catId);

    /**
     * 校验表达式语法（不落库）
     * <p>供编辑器边输入边校验。
     *
     * @param checkExpr 待校验表达式
     * @return 校验结果
     */
    ExprValidateVO validateExpr(String checkExpr);
}
