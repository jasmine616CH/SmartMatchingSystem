package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.PartSupplierPrice;
import module.scheme.vo.PriceVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PartSupplierPriceMapper extends BaseMapper<PartSupplierPrice> {

    /**
     * 根据part_id查询所有的价格信息
     * @param partIds
     * @return 返回价格id键
     */
    @MapKey("part_id")
    Map<Long, PriceVO> selectPriceByPartIds(@Param("partIds")List<Long> partIds);
}
