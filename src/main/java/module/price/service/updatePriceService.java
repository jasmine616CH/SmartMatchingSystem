package module.price.service;

import module.price.dto.updatePriceDTO;

/**
 * 价格更新业务接口
 * 实现价格的更新维护
 */
public interface updatePriceService {

    /**
     * 更新价格信息
     * @param dto 价格信息
     */
    void updatePrice(updatePriceDTO dto);

}
