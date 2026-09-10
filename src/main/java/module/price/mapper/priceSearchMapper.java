package module.price.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import module.price.dto.SearchPriceDateDTO;
import module.price.dto.ViewPriceDateDTO;
import module.price.entity.PartSupplierPrice;
import module.price.vo.PriceDateVO;
import module.price.vo.SearchPriceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 关联配件供应商报价表 (part_supplier_price)、配件三级分类表(part_category)、配件档案主表(part_info)
 *  、配件-供应商关联表(part_supplier)、供应商主体表(supplier）
 */
@Mapper
public interface PriceSearchMapper extends BaseMapper<PartSupplierPrice> {

    /**
     * 根据配件名称分页查询配件价格和供应商
     * <p>
     * page 必须是首个入参，分页插件据此改写 SQL 并回填 total；
     * dto 加 @Param 与 XML 里的 #{dto.xxx} 对应。
     *
     * @param page 分页对象
     * @param dto  查询信息
     * @return 分页结果（即入参 page，records 已填充）
     */
    Page<SearchPriceVO> selectPartPrice(Page<SearchPriceVO> page,
            @Param("dto") SearchPriceDateDTO dto);

    /**
     * 根据配件名字查询配件价格详细信息
     * @param dto 配件名字
     * @return 详细信息
     */
    PriceDateVO viewPrice(@Param("dto") ViewPriceDateDTO dto);

}
