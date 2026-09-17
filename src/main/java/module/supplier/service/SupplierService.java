package module.supplier.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.supplier.dto.SupplierQueryDTO;
import module.supplier.dto.SupplierSaveDTO;
import module.supplier.dto.SupplierUpdateDTO;
import module.supplier.vo.SupplierDetailVO;
import module.supplier.vo.SupplierListVO;

/**
 * 供应商主体业务接口
 * 实现供应商的增删改查与审核流转
 */
public interface SupplierService {

    /**
     * 分页查询供应商列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    Page<SupplierListVO> querySupplierList(SupplierQueryDTO dto);

    /**
     * 查询供应商详情（含联系人列表）
     *
     * @param supplierId 供应商主键ID
     * @return 供应商详情
     */
    SupplierDetailVO querySupplierDetail(Long supplierId);

    /**
     * 新增供应商（同时写入联系人），初始状态为草稿
     *
     * @param dto 供应商信息
     */
    void addSupplier(SupplierSaveDTO dto);

    /**
     * 修改供应商（联系人整体替换）
     *
     * @param dto 供应商信息
     */
    void updateSupplier(SupplierUpdateDTO dto);

    /**
     * 删除供应商（仅草稿状态可删）
     *
     * @param supplierId 供应商主键ID
     */
    void deleteSupplier(Long supplierId);

    /**
     * 提交审核：草稿 → 待审核
     *
     * @param supplierId 供应商主键ID
     */
    void submitAudit(Long supplierId);

    /**
     * 撤回：已发布 → 草稿
     *
     * @param supplierId 供应商主键ID
     */
    void revoke(Long supplierId);

    /**
     * 撤销申请：待审核 → 草稿
     *
     * @param supplierId 供应商主键ID
     */
    void cancelSubmit(Long supplierId);
}
