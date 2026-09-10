package module.price.service.impl;

import module.price.dto.UpdatePriceDTO;
import module.price.mapper.UpdatePriceMapper;
import module.price.service.UpdatePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 价格更新业务接口
 * 实现价格的更新维护
 */
@Service
public class UpdatePriceServiceImpl implements UpdatePriceService {

    @Autowired
    private UpdatePriceMapper UpdatePriceMapper;

    /**
     * 更新价格信息
     * @param dto 价格信息
     */
    @Override
    public void updatePrice(UpdatePriceDTO dto) {
        UpdatePriceMapper.updatePrice(dto);
    }
}
