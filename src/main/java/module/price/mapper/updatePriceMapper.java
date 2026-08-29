package module.price.mapper;

import module.price.dto.updatePriceDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 关联配件供应商报价表 (part_supplier_price)、配件三级分类表(part_category)、配件档案主表(part_info)
 *  、配件-供应商关联表(part_supplier)、供应商主体表(supplier）
 */
@Mapper
public interface updatePriceMapper {

    /**
     * 更新价格
     * @param dto 更改信息
     */
    void updatePrice(updatePriceDTO dto);

}
