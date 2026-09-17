package module.compat.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.compat.dto.ConflictRuleQueryDTO;
import module.compat.entity.ConflictRule;
import module.compat.vo.ConflictRuleVO;
import module.compat.vo.ParamCodeOptionVO;
import module.compat.vo.SubsystemVO;

/**
 * 配件兼容冲突规则表 (conflict_rule)
 */
@Mapper
public interface ConflictRuleMapper extends BaseMapper<ConflictRule> {

    /**
     * 分页查询冲突规则（联 part_category 带出两侧子系统名称）
     * <p>
     * page 必须是首个入参，分页插件据此改写 SQL 并回填 total。
     *
     * @param page 分页对象
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<ConflictRuleVO> selectRulePage(Page<ConflictRuleVO> page,
            @Param("dto") ConflictRuleQueryDTO dto);

    /**
     * 查询单条冲突规则（带两侧子系统名称）
     *
     * @param ruleId 规则主键ID
     * @return 规则详情，不存在返回 null
     */
    ConflictRuleVO selectRuleById(@Param("ruleId") Long ruleId);

    /**
     * 按方案中实际出现的子系统组合，捞出命中的启用规则
     *
     * @param catAIds A侧二级子系统集合
     * @param catBIds B侧二级子系统集合
     * @return 命中的规则（含两侧子系统名称）
     */
    List<ConflictRuleVO> selectEnabledByCatPairs(@Param("catAIds") Collection<Long> catAIds,
            @Param("catBIds") Collection<Long> catBIds);

    // ==================== 规则编辑器的辅助查询 ====================

    /**
     * 查询全部二级子系统（规则只能挂在这一层）
     *
     * @return 二级子系统列表
     */
    List<SubsystemVO> selectSubsystems();

    /**
     * 查询某二级子系统下所有已发布模板中出现过的参数编码
     * <p>供表达式编辑器做参数编码候选，避免用户手敲编码。
     *
     * @param catId 二级子系统ID
     * @return 参数编码候选列表
     */
    List<ParamCodeOptionVO> selectParamCodesBySubsystem(@Param("catId") Long catId);
}
