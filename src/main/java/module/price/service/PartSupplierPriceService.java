package module.price.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.price.dto.PartSupplierPriceQueryDTO;
import module.price.dto.PartSupplierPriceSaveDTO;
import module.price.dto.PartSupplierPriceUpdateDTO;
import module.price.vo.PartSupplierPriceDetailVO;
import module.price.vo.PartSupplierPriceListVO;

/**
 * 配件供应商报价业务接口
 * 实现报价的增删改查
 */
public interface PartSupplierPriceService {

    /**
     * 分页查询报价列表
     *
     * @param dto 查询条件，全部可选
     * @return 分页结果
     */
    Page<PartSupplierPriceListVO> queryPriceList(PartSupplierPriceQueryDTO dto);

    /**
     * 查询报价详情
     *
     * @param priceId 报价主键ID
     * @return 报价详情
     */
    PartSupplierPriceDetailVO queryPriceDetail(Long priceId);

    /**
     * 新增报价
     *
     * @param dto 报价信息
     */
    void addPrice(PartSupplierPriceSaveDTO dto);

    /**
     * 修改报价
     *
     * @param dto 报价信息
     */
    void updatePrice(PartSupplierPriceUpdateDTO dto);

    /**
     * 删除报价
     *
     * @param priceId 报价主键ID
     */
    void deletePrice(Long priceId);
}
