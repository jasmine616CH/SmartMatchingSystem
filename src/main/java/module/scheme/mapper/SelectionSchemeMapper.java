package module.scheme.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.scheme.dto.SchemeQueryDTO;
import module.scheme.entity.SelectionScheme;
import module.scheme.vo.SchemeBriefVO;
import module.scheme.vo.SchemeDetailVO;

/**
 * 对应选型方案主表(selection_scheme)
 * <p>
 * 单表增删改继承 BaseMapper；列表与详情需要联 sys_user 取创建人姓名并聚合
 * 明细数量与总价，写在 resources/mapper/scheme/SelectionSchemeMapper.xml。
 */
@Mapper
public interface SelectionSchemeMapper extends BaseMapper<SelectionScheme> {

    /**
     * 分页查询方案列表（聚合明细数量、总价、是否含正式BOM）
     * <p>
     * page 必须是首个入参，分页插件据此改写 SQL 并回填 total。
     *
     * @param page 分页对象
     * @param dto  查询条件
     * @return 分页结果
     */
    Page<SchemeBriefVO> selectSchemePage(Page<SchemeBriefVO> page,
            @Param("dto") SchemeQueryDTO dto);

    /**
     * 查询方案详情（联创建人姓名并聚合明细数量与总价）
     *
     * @param schemeId 方案主键ID
     * @return 详情，不存在返回 null
     */
    SchemeDetailVO selectSchemeDetail(@Param("schemeId") Long schemeId);
}
