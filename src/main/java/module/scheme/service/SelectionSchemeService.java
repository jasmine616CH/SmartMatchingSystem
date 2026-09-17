package module.scheme.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.scheme.dto.SchemeCompareQueryDTO;
import module.scheme.dto.SchemeCopyDTO;
import module.scheme.dto.SchemePartQuantityDTO;
import module.scheme.dto.SchemePartQueryDTO;
import module.scheme.dto.SchemeQueryDTO;
import module.scheme.dto.SchemeSaveSelectionDTO;
import module.scheme.dto.SchemeUpdateDTO;
import module.scheme.vo.SchemeBriefVO;
import module.scheme.vo.SchemeCompareVO;
import module.scheme.vo.SchemeDetailVO;
import module.scheme.vo.SchemePartVO;
import module.scheme.vo.SchemeSummaryVO;
import module.scheme.vo.SchemeValidateResultVO;

/**
 * 选型方案业务接口
 */
public interface SelectionSchemeService {

    /**
     * 保存选配结果为方案
     *
     * @param dto 选配结果
     * @return 新建的方案ID
     */
    Long saveSelection(SchemeSaveSelectionDTO dto);

    /**
     * 分页查询方案列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    Page<SchemeBriefVO> querySchemeList(SchemeQueryDTO dto);

    /**
     * 查询方案详情
     *
     * @param schemeId 方案主键ID
     * @return 方案详情
     */
    SchemeDetailVO querySchemeDetail(Long schemeId);

    /**
     * 分页查询方案配件明细
     *
     * @param schemeId 方案主键ID
     * @param dto      查询条件
     * @return 分页结果
     */
    Page<SchemePartVO> querySchemePartPage(Long schemeId, SchemePartQueryDTO dto);

    /**
     * 修改方案基本信息
     *
     * @param dto 方案信息
     */
    void updateScheme(SchemeUpdateDTO dto);

    /**
     * 删除方案（级联删除明细与冲突日志）
     *
     * @param schemeId 方案主键ID
     */
    void deleteScheme(Long schemeId);

    /**
     * 批量修改明细数量
     *
     * @param schemeId 方案主键ID
     * @param dto      明细数量
     * @return 修改后的方案汇总
     */
    SchemeSummaryVO updatePartQuantity(Long schemeId, SchemePartQuantityDTO dto);

    /**
     * 删除方案中的配件明细
     *
     * @param schemeId     方案主键ID
     * @param schemePartId 明细主键ID
     * @return 删除后的方案汇总
     */
    SchemeSummaryVO deleteSchemePart(Long schemeId, Long schemePartId);

    /**
     * 方案参数合法性校验
     *
     * @param schemeId 方案主键ID
     * @return 校验结果
     */
    SchemeValidateResultVO validateScheme(Long schemeId);

    /**
     * 候选件并排比较
     * <p>
     * 返回「参数 × 配件」矩阵，每个格子标注满足/临界/不满足/缺失。
     *
     * @param dto 比较入参
     * @return 比较矩阵
     */
    SchemeCompareVO compareSchemes(SchemeCompareQueryDTO dto);

    /**
     * 复制方案（历史方案复用）
     * <p>明细整体复制，原方案不受影响。
     *
     * @param schemeId 被复制的方案ID
     * @param dto      新方案信息
     * @return 新方案ID
     */
    Long copyScheme(Long schemeId, SchemeCopyDTO dto);
}
