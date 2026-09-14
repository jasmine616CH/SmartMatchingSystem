package module.scheme.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.scheme.dto.SchemePartQueryDTO;
import module.scheme.entity.SchemePart;
import module.scheme.vo.SchemePartVO;

/**
 * 对应方案候选配件明细表(scheme_part)
 */
@Mapper
public interface SchemePartMapper extends BaseMapper<SchemePart> {

    /**
     * 分页查询方案配件明细（联配件、分类、单价，支持排序与筛选）
     * <p>
     * 排序字段由服务端白名单映射成真实列名后再拼入，不用 ${} 直接拼前端入参。
     *
     * @param page     分页对象
     * @param schemeId 方案主键ID
     * @param dto      查询条件
     * @return 分页结果
     */
    Page<SchemePartVO> selectSchemePartPage(Page<SchemePartVO> page,
            @Param("schemeId") Long schemeId,
            @Param("dto") SchemePartQueryDTO dto);

    /**
     * 查询方案下纳入正式BOM的配件明细（导出用，不分页）
     *
     * @param schemeId 方案主键ID
     * @return 明细列表
     */
    List<SchemePartVO> selectBomParts(@Param("schemeId") Long schemeId);

    /**
     * 统计方案下的配件明细数量
     *
     * @param schemeId 方案主键ID
     * @return 明细条数
     */
    int countBySchemeId(@Param("schemeId") Long schemeId);
}
