package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.VehicleParamDef;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应无人车顶层参数定义表(vehicle_param_def)
 */
@Mapper
public interface VehicleParamDefMapper extends BaseMapper<VehicleParamDef> {
}
