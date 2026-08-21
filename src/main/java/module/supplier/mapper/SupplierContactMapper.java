package module.supplier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.supplier.dto.AdminSupplierDTO;
import module.supplier.entity.SupplierContact;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应供应商联系人表(supplier-contact)
 */
@Mapper
public interface SupplierContactMapper extends BaseMapper<SupplierContact> {

    /**
     * 新增供应商联系方式
     * @param dto 供应商联系人信息
     */
    @Insert("insert into ugvc_db.supplier_contact(contact_id, supplier_id, name, position, phone, email) " +
            "VALUES (#{contact_id},#{supplier_id},#{name},#{position},#{phone},#{email})")
    void addSupplierContact(AdminSupplierDTO dto);
}
