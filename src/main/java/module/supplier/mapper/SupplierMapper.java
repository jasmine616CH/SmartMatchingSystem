package module.supplier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.supplier.entity.supplier;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SupplierMapper extends BaseMapper<supplier> {
}
