package module.supplier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.supplier.entity.SupplierQualification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应供应商资质档案表表(supplier-qualification)
 */
@Mapper
public interface SupplierQualificationMapper extends BaseMapper<SupplierQualification> {
}
