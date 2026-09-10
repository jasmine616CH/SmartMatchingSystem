package module.price.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;
import module.price.dto.SearchPriceDateDTO;
import module.price.dto.ViewPriceDateDTO;
import module.price.mapper.PriceSearchMapper;
import module.price.service.PriceSearchService;
import module.price.vo.PriceDateVO;
import module.price.vo.SearchPriceVO;
import org.springframework.stereotype.Service;

/**
 * 配件价格业务实现类
 * 实现价格查看
 */
@RequiredArgsConstructor 
@Service
public class PriceSearchServiceImpl implements PriceSearchService {

    private final PriceSearchMapper PriceSearchMapper;

    /**
     * 查找配件价格信息
     * @param dto 配件信息
     * @return 成功返回相关信息
     */
    @Override
    public Page<SearchPriceVO> searchPriceDate(SearchPriceDateDTO dto) {

        //1.构造分页对象，Page 作为 Mapper 首个入参交给 PaginationInnerInterceptor 处理
        Page<SearchPriceVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        //2.执行查询，插件自动改写 SQL 并回填 total/records
        return PriceSearchMapper.selectPartPrice(page, dto);
    }

    /**
     * 查看价格详细信息
     * @param dto 配件名字
     * @return 成功返回详细信息
     */
    @Override
    public PriceDateVO viewPriceDate(ViewPriceDateDTO dto) {
        return PriceSearchMapper.viewPrice(dto);
    }
}
