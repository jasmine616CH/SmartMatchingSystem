package module.supplier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.supplier.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
}
