package module.supplier.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.supplier.dto.SupplierQualificationQueryDTO;
import module.supplier.dto.SupplierQualificationSaveDTO;
import module.supplier.dto.SupplierQualificationUpdateDTO;
import module.supplier.vo.SupplierQualificationVO;

/**
 * 供应商资质业务接口
 * 实现供应商资质的增删改查与审核流转
 */
public interface SupplierQualificationService {

    /**
     * 分页查询供应商资质列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    Page<SupplierQualificationVO> queryQualificationList(SupplierQualificationQueryDTO dto);

    /**
     * 查询供应商资质详情
     *
     * @param qfId 资质主键ID
     * @return 资质详情
     */
    SupplierQualificationVO queryQualificationDetail(Long qfId);

    /**
     * 新增供应商资质，初始状态为草稿
     *
     * @param dto 资质信息
     */
    void addQualification(SupplierQualificationSaveDTO dto);

    /**
     * 修改供应商资质
     *
     * @param dto 资质信息
     */
    void updateQualification(SupplierQualificationUpdateDTO dto);

    /**
     * 删除供应商资质（仅草稿状态可删）
     *
     * @param qfId 资质主键ID
     */
    void deleteQualification(Long qfId);

    /**
     * 提交审核：草稿 → 待审核
     *
     * @param qfId 资质主键ID
     */
    void submitAudit(Long qfId);

    /**
     * 撤回：有效 → 草稿
     *
     * @param qfId 资质主键ID
     */
    void revoke(Long qfId);

    /**
     * 撤销申请：待审核 → 草稿
     *
     * @param qfId 资质主键ID
     */
    void cancelSubmit(Long qfId);
}
