package module.part.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.part.entity.PartInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PartInfoMapper extends BaseMapper<PartInfo> {
}
