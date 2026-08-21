package module.supplier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.supplier.dto.AdminSupplierDTO;
import module.supplier.entity.supplier;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应供应商主体表(supplier)
 */
@Mapper
public interface SupplierMapper extends BaseMapper<supplier> {

    /**
     * 新增供应商
     * @param dto 供应商信息
     */
    @Insert("insert into ugvc_db.supplier(supplier_id, supplier_name, supply_scope, credit_code, address, status, remark) " +
            "VALUES (#{supplier_id},#{supplier_name},#{supplier_scope},#{credit_code},#{address},1,#{remark})")
    void addSupplier(AdminSupplierDTO dto);
}
