package module.price.controller;

import com.github.pagehelper.PageInfo;
import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.price.dto.searchPriceDateDTO;
import module.price.dto.viewPriceDateDTO;
import module.price.service.priceSearchService;
import module.price.vo.priceDateVO;
import module.price.vo.searchPriceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配件价格业务控制器
 * 实现价格查询
 */
@RequestMapping("/price")
@RequiredArgsConstructor
@RestController
@Slf4j
public class priceSearchController {

    @Autowired
    private priceSearchService priceSearchService;

    /**
     * 根据配件名称分页查询配件价格和供应商
     * @param dto 配件信息
     * @return 成功返回相关数据
     */
    @GetMapping("/search")
    public Result<PageInfo<searchPriceVO>> searchPrice(searchPriceDateDTO dto){
        return Result.success(priceSearchService.searchPriceDate(dto));
    }

    /**
     * 查看配件价格详情
     * @param dto 配件名字
     * @return 成功返回相关详细信息
     */
    @GetMapping("/view")
    public Result<priceDateVO> viewPriceDate(viewPriceDateDTO dto){
        return Result.success(priceSearchService.viewPriceDate(dto));
    }

}
