package module.price.service;

import com.github.pagehelper.PageInfo;
import module.price.dto.searchPriceDateDTO;
import module.price.dto.viewPriceDateDTO;
import module.price.vo.priceDateVO;
import module.price.vo.searchPriceVO;

/**
 * 配件价格业务接口
 * 实现价格查看
 */
public interface priceSearchService {

    /**
     * 查找配件价格信息
     * @param dto 配件信息
     * @return 成功返回相关数据
     */
    PageInfo<searchPriceVO> searchPriceDate(searchPriceDateDTO dto);

    /**
     * 查看配件价格详情
     * @param dto 配件名字
     * @return 成功返回相关详细信息
     */
    priceDateVO viewPriceDate(viewPriceDateDTO dto);

}
