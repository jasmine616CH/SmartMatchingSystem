package module.supplier.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import module.supplier.entity.SupplierContact;

/**
 * 对应供应商联系人表(supplier_contact)
 * <p>
 * 原实现的 @Insert 使用 #{contact_id} / #{supplier_id} 下划线占位符，
 * 与 DTO 的驼峰属性对不上，且硬编码了 ugvc_db. 库名，已移除，统一走 BaseMapper。
 */
@Mapper
public interface SupplierContactMapper extends BaseMapper<SupplierContact> {
}
