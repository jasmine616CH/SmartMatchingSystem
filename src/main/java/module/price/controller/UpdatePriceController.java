package module.price.controller;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.price.dto.UpdatePriceDTO;
import module.price.service.UpdatePriceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 价格更新业务控制器
 * 实现价格的更新维护
 */
@RequestMapping("/api/price")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UpdatePriceController {

    private final UpdatePriceService UpdatePriceService;

    @PostMapping("/update")
    public Result<String> updatePrice(UpdatePriceDTO dto){
        UpdatePriceService.updatePrice(dto);
        return Result.success("更新成功");
    }

}
