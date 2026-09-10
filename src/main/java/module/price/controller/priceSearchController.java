package module.price.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.price.dto.SearchPriceDateDTO;
import module.price.dto.ViewPriceDateDTO;
import module.price.service.PriceSearchService;
import module.price.vo.PriceDateVO;
import module.price.vo.SearchPriceVO;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Slf4j
public class PriceSearchController {

    private final PriceSearchService priceSearchService;

    /**
     * 根据配件名称分页查询配件价格和供应商
     * @param dto 配件信息
     * @return 成功返回相关数据
     */
    @GetMapping("/search")
    public Result<Page<SearchPriceVO>> searchPrice(@Valid SearchPriceDateDTO dto){
        return Result.success(priceSearchService.searchPriceDate(dto));
    }

    /**
     * 查看配件价格详情
     * @param dto 配件名字
     * @return 成功返回相关详细信息
     */
    @GetMapping("/view")
    public Result<PriceDateVO> viewPriceDate(ViewPriceDateDTO dto){
        return Result.success(priceSearchService.viewPriceDate(dto));
    }
}
