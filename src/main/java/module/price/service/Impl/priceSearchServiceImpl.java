package module.price.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import module.price.dto.searchPriceDateDTO;
import module.price.dto.viewPriceDateDTO;
import module.price.mapper.priceSearchMapper;
import module.price.service.priceSearchService;
import module.price.vo.priceDateVO;
import module.price.vo.searchPriceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 配件价格业务实现类
 * 实现价格查看
 */
@Service
public class priceSearchServiceImpl implements priceSearchService {

    @Autowired
    private priceSearchMapper priceSearchMapper;

    /**
     * 查找配件价格信息
     * @param dto 配件信息
     * @return 成功返回相关信息
     */
    @Override
    public PageInfo<searchPriceVO> searchPriceDate(searchPriceDateDTO dto) {

        //1.自动分页
        PageHelper.startPage(dto.getPageNum(),dto.getPageSize());

        //2.执行查语句
        List<searchPriceVO> list = priceSearchMapper.selectPartPrice(dto);

        //3.返回封装
        return new PageInfo<>(list);
    }

    /**
     * 查看价格详细信息
     * @param dto 配件名字
     * @return 成功返回详细信息
     */
    @Override
    public priceDateVO viewPriceDate(viewPriceDateDTO dto) {
        return priceSearchMapper.viewPrice(dto);
    }
}
