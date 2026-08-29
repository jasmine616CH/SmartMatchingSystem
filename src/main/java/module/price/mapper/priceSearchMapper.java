package module.price.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.price.dto.searchPriceDateDTO;
import module.price.dto.viewPriceDateDTO;
import module.price.entity.PartSupplierPrice;
import module.price.vo.priceDateVO;
import module.price.vo.searchPriceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 关联配件供应商报价表 (part_supplier_price)、配件三级分类表(part_category)、配件档案主表(part_info)
 *  、配件-供应商关联表(part_supplier)、供应商主体表(supplier）
 */
@Mapper
public interface priceSearchMapper extends BaseMapper<PartSupplierPrice> {

    /**
     * 根据配件名称分页查询配件价格和供应商
     * @param dto 查询信息
     * @return 分页结果
     */
    List<searchPriceVO> selectPartPrice(searchPriceDateDTO dto);

    /**
     * 根据配件名字查询配件价格详细信息
     * @param dto 配件名字
     * @return 详细信息
     */
    priceDateVO viewPrice(viewPriceDateDTO dto);

}
