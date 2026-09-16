package module.part.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.part.entity.PartInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.part.vo.PartInfoDetailVO;
import module.part.vo.PartInfoListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PartInfoMapper extends BaseMapper<PartInfo> {

    /**
     * 查询配件档案详情（联出分类名、模板名、维护人/审批人姓名）
     *
     * @param partId 配件主键ID
     * @return 详情，不存在返回 null
     */
    PartInfoDetailVO selectPartDetail(@Param("partId") Long partId);

    /**
     * 分页查询某分类下的配件列表（联出分类名与维护人姓名）
     * <p>
     * page 必须是首个入参，分页插件据此改写 SQL 并回填 total。
     *
     * @param page   分页对象
     * @param catId  三级配件分类ID
     * @return 分页结果
     */
    Page<PartInfoListVO> selectPartPage(Page<PartInfoListVO> page, @Param("catId") Long catId);
}
