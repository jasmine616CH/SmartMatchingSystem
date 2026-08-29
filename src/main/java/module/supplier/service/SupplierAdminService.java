package module.supplier.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import module.supplier.dto.AdminSupplierDTO;
import module.supplier.dto.QuerySupplierDTO;
import module.supplier.dto.SupplierCodeDTO;
import module.supplier.dto.SupplierUpdateDateDTO;
import module.supplier.vo.QuerySupplierVo;
import module.supplier.vo.SupplierDateVo;

/**
 * 供应商管理员业务接口
 * 实现对供应商账号的管理
 */
public interface SupplierAdminService {

    /**
     * 查询供应商信息
     *
     * @return 返回用户信息
     */
    Page<QuerySupplierVo> searchSupplierList(QuerySupplierDTO dto);

    /**
     * 删除供应商
     *
     * @return 返回成功相关信息
     */
    void deleteSupplier(SupplierCodeDTO dto);

    /**
     * 修改供应商信息
     *
     * @return 返沪成功信息
     */
    void updateSupplierDate(SupplierUpdateDateDTO dto);

    /**
     * 查看供应商信息
     * @param dto 供应商id
     * @return 返回供应商信息
     */
    SupplierDateVo viewSupplierDate(SupplierCodeDTO dto);

    /**
     * 添加供应商
     *
     * @param dto 供应商信息
     */
    void addSupplierAccount(AdminSupplierDTO dto);

}
