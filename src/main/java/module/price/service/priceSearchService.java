package module.price.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.price.dto.SearchPriceDateDTO;
import module.price.dto.ViewPriceDateDTO;
import module.price.vo.PriceDateVO;
import module.price.vo.SearchPriceVO;

/**
 * 配件价格业务接口
 * 实现价格查看
 */
public interface PriceSearchService {

    /**
     * 查找配件价格信息
     * @param dto 配件信息
     * @return 成功返回相关数据
     */
    Page<SearchPriceVO> searchPriceDate(SearchPriceDateDTO dto);

    /**
     * 查看配件价格详情
     * @param dto 配件名字
     * @return 成功返回相关详细信息
     */
    PriceDateVO viewPriceDate(ViewPriceDateDTO dto);

}
