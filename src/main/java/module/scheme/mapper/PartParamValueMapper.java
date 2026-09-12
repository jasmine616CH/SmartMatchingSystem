package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.PartParamValue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartParamValueMapper extends BaseMapper<PartParamValue> {

    /**
     * 根据part_id查询所有参数信息
     * @param partIds
     * @return
     */
    List<PartParamValue> selectByPartIds(List<Long> partIds);
}
