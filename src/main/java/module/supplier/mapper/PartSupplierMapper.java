package module.supplier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import module.supplier.entity.PartSupplier;

import org.apache.ibatis.annotations.Mapper;

/**
 * 对应配件-供应商关联表(part-supplier)
 */
@Mapper
public interface PartSupplierMapper extends BaseMapper<PartSupplier> {
}
