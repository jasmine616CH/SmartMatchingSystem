package module.price.controller;

import common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.price.dto.updatePriceDTO;
import module.price.service.updatePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 价格更新业务控制器
 * 实现价格的更新维护
 */
@RequestMapping("/price")
@RequiredArgsConstructor
@RestController
@Slf4j
public class updatePriceController {

    @Autowired
    private updatePriceService updatePriceService;

    @PostMapping("/update")
    public Result<String> updatePrice(updatePriceDTO dto){
        updatePriceService.updatePrice(dto);
        return Result.success("更新成功");
    }

}
