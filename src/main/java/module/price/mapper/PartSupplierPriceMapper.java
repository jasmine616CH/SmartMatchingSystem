package module.price.mapper;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.price.dto.PartSupplierPriceQueryDTO;
import module.price.entity.PartSupplierPrice;
import module.price.vo.PartSupplierPriceDetailVO;
import module.price.vo.PartSupplierPriceListVO;

/**
 * 配件供应商报价表 (part_supplier_price)
 * <p>
 * 单表增删改查继承 BaseMapper；联表查询与区间重叠校验写在
 * resources/mapper/price/PartSupplierPriceMapper.xml。
 */
@Mapper
public interface PartSupplierPriceMapper extends BaseMapper<PartSupplierPrice> {

    /**
     * 分页查询报价列表（联表带出配件、分类、供应商、录入人信息）
     * <p>
     * page 必须是首个入参，分页插件据此改写 SQL 并回填 total；
     * dto 加 @Param 与 XML 里的 #{dto.xxx} 对应。
     *
     * @param page 分页对象
     * @param dto  查询条件，全部可选
     * @return 分页结果（即入参 page，records 已填充）
     */
    Page<PartSupplierPriceListVO> selectPricePage(Page<PartSupplierPriceListVO> page,
            @Param("dto") PartSupplierPriceQueryDTO dto);

    /**
     * 查询报价详情
     *
     * @param priceId 报价主键ID
     * @return 详情，不存在返回 null
     */
    PartSupplierPriceDetailVO selectPriceDetail(@Param("priceId") Long priceId);

    /**
     * 统计同配件供应商、同价格类型下与新报价日期区间重叠的报价条数
     * <p>
     * 区间为闭区间，首尾相接（旧记录 expire_date 恰等于新记录 effect_date）也计为重叠，
     * 保证任意时刻不会出现两条同时生效的报价。校验范围不含其他价格类型，
     * 因此同一配件供应商可并存 standard 与 agreement 两种报价。
     * <p>
     * 注意：并发场景下两个新增可能同时通过本校验，唯一索引无法表达日期区间规则，
     * 如需彻底消除需引入悲观锁或串行化隔离级别。
     *
     * @param psId           配件供应商关联主键
     * @param priceType      价格类型
     * @param effectDate     新报价生效日期
     * @param expireDate     新报价失效日期，null 表示永久有效
     * @param excludePriceId 修改场景下需排除的自身报价ID，新增时传 null
     * @return 重叠条数
     */
    long countOverlap(@Param("psId") Long psId,
            @Param("priceType") String priceType,
            @Param("effectDate") LocalDate effectDate,
            @Param("expireDate") LocalDate expireDate,
            @Param("excludePriceId") Long excludePriceId);

    /**
     * 查询配件-供应商关联的认证状态
     * <p>
     * 一次查询同时覆盖两种校验：返回 null 表示关联不存在；返回值不为 1 表示未认证。
     *
     * @param psId 配件供应商关联主键
     * @return 认证状态，关联不存在时返回 null
     */
    @Select("SELECT auth_status FROM part_supplier WHERE ps_id = #{psId}")
    Integer selectAuthStatusByPsId(@Param("psId") Long psId);
}
