package module.price.service.impl;

import module.price.dto.updatePriceDTO;
import module.price.mapper.updatePriceMapper;
import module.price.service.updatePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 价格更新业务接口
 * 实现价格的更新维护
 */
@Service
public class updatePriceServiceImpl implements updatePriceService {

    @Autowired
    private updatePriceMapper updatePriceMapper;

    /**
     * 更新价格信息
     * @param dto 价格信息
     */
    @Override
    public void updatePrice(updatePriceDTO dto) {
        updatePriceMapper.updatePrice(dto);
    }
}
