package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.PartInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PartInfoMapper extends BaseMapper<PartInfo> {

    /**
     * 查询partId
     * @param catId
     * @return 返回所需的id列表
     */
    List<Long> selectPartIdsByCatId(Long catId);

    /**
     * 查询配件信息
     * @param partIds
     * @return 返回配件信息
     */
    List<PartInfo> selectPartInfoByIds(List<Long> partIds);
}
