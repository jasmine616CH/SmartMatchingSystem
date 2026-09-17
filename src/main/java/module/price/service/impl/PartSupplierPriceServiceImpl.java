package module.price.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.SecurityUtils;
import lombok.RequiredArgsConstructor;
import module.price.dto.PartSupplierPriceQueryDTO;
import module.price.dto.PartSupplierPriceSaveDTO;
import module.price.dto.PartSupplierPriceUpdateDTO;
import module.price.entity.PartSupplierPrice;
import module.price.mapper.PartSupplierPriceMapper;
import module.price.service.PartSupplierPriceService;
import module.price.vo.PartSupplierPriceDetailVO;
import module.price.vo.PartSupplierPriceListVO;

/**
 * 配件供应商报价业务实现类
 * 实现报价的增删改查
 */
@RequiredArgsConstructor
@Service
public class PartSupplierPriceServiceImpl implements PartSupplierPriceService {

    private final PartSupplierPriceMapper partSupplierPriceMapper;

    /** 计价币种默认值 */
    private static final String DEFAULT_CURRENCY = "CNY";

    /** 认证通过状态值 */
    private static final int AUTH_STATUS_PASSED = 1;

    /** 单价上限，对应 price_value 列 decimal(18,2) */
    private static final BigDecimal MAX_PRICE_VALUE = new BigDecimal("9999999999999999.99");

    /**
     * 分页查询报价列表
     *
     * @param dto 查询条件，全部可选
     * @return 分页结果
     */
    @Override
    public Page<PartSupplierPriceListVO> queryPriceList(PartSupplierPriceQueryDTO dto) {
        // DTO 可能为 null（无任何查询参数时），此处兜底默认分页，避免空指针
        PartSupplierPriceQueryDTO query = dto == null ? new PartSupplierPriceQueryDTO() : dto;

        // 1.构造分页对象，Page 作为 Mapper 首个入参交给 PaginationInnerInterceptor 处理
        Page<PartSupplierPriceListVO> page = new Page<>(query.getPageNum(), query.getPageSize());

        // 2.执行查询，插件自动改写 SQL 并回填 total/records
        return partSupplierPriceMapper.selectPricePage(page, query);
    }

    /**
     * 查询报价详情
     *
     * @param priceId 报价主键ID
     * @return 报价详情
     */
    @Override
    public PartSupplierPriceDetailVO queryPriceDetail(Long priceId) {
        if (priceId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "priceId 不能为空");
        }
        PartSupplierPriceDetailVO detail = partSupplierPriceMapper.selectPriceDetail(priceId);
        if (detail == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "报价记录不存在");
        }
        return detail;
    }

    /**
     * 新增报价
     *
     * @param dto 报价信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPrice(PartSupplierPriceSaveDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        checkSupplierAuth(dto.getPsId());
        validateDateRange(dto.getEffectDate(), dto.getExpireDate());
        validateNumbers(dto.getPriceValue(), dto.getMoq(), dto.getLeadTime());
        checkOverlap(dto.getPsId(), dto.getPriceType(),
                dto.getEffectDate(), dto.getExpireDate(), null);

        PartSupplierPrice entity = BeanUtil.toBean(dto, PartSupplierPrice.class);
        if (!StringUtils.hasText(entity.getCurrency())) {
            entity.setCurrency(DEFAULT_CURRENCY);
        }
        // 录入人由登录态派生，不接受前端传入
        entity.setCreateUserId(SecurityUtils.getCurrentUserId());

        int rows = partSupplierPriceMapper.insert(entity);
        if (rows == 0) {
            throw new BusinessException(ResultCode.ERROR, "新增报价失败");
        }
    }

    /**
     * 修改报价
     *
     * @param dto 报价信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePrice(PartSupplierPriceUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        Long priceId = dto.getPriceId();
        if (priceId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "priceId 不能为空");
        }
        if (partSupplierPriceMapper.selectById(priceId) == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "报价记录不存在");
        }
        // psId 可能被改指向，关联与认证状态必须重新校验
        checkSupplierAuth(dto.getPsId());
        validateDateRange(dto.getEffectDate(), dto.getExpireDate());
        validateNumbers(dto.getPriceValue(), dto.getMoq(), dto.getLeadTime());
        checkOverlap(dto.getPsId(), dto.getPriceType(),
                dto.getEffectDate(), dto.getExpireDate(), priceId);

        // 不设置 createUserId：DTO 无此字段，NOT_NULL 更新策略会保留原录入人
        PartSupplierPrice entity = BeanUtil.toBean(dto, PartSupplierPrice.class);
        if (!StringUtils.hasText(entity.getCurrency())) {
            entity.setCurrency(DEFAULT_CURRENCY);
        }

        int rows = partSupplierPriceMapper.updateById(entity);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "报价记录不存在");
        }
    }

    /**
     * 删除报价
     *
     * @param priceId 报价主键ID
     */
    @Override
    public void deletePrice(Long priceId) {
        if (priceId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "priceId不能为空");
        }
        if (partSupplierPriceMapper.selectById(priceId) == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "报价记录不存在");
        }
        int rows = partSupplierPriceMapper.deleteById(priceId);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "报价记录不存在");
        }
    }

    /**
     * 校验配件-供应商关联存在且已通过认证
     *
     * @param psId 配件供应商关联主键
     */
    private void checkSupplierAuth(Long psId) {
        if (psId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "psId 不能为空");
        }
        Integer authStatus = partSupplierPriceMapper.selectAuthStatusByPsId(psId);
        if (authStatus == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件供应商关联不存在：" + psId);
        }
        if (authStatus != AUTH_STATUS_PASSED) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该配件-供应商关联未通过认证，不允许录入报价");
        }
    }

    /**
     * 校验报价生效/失效日期的合法性
     *
     * @param effectDate 生效日期
     * @param expireDate 失效日期，可为空表示永久有效
     */
    private void validateDateRange(LocalDate effectDate, LocalDate expireDate) {
        if (effectDate == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "effectDate 不能为空");
        }
        if (expireDate != null && !expireDate.isAfter(effectDate)) {
            throw new BusinessException(ResultCode.PARAM_RANGE_ERROR, "失效日期必须晚于生效日期");
        }
    }

    /**
     * 校验报价数值字段的合法性
     *
     * @param priceValue 单价
     * @param moq        最小起订量
     * @param leadTime   交付周期
     */
    private void validateNumbers(BigDecimal priceValue, Integer moq, Integer leadTime) {
        if (priceValue == null || priceValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.PARAM_RANGE_ERROR, "单价必须大于0");
        }
        if (priceValue.compareTo(MAX_PRICE_VALUE) > 0) {
            throw new BusinessException(ResultCode.PARAM_RANGE_ERROR, "单价超出可存储范围");
        }
        if (moq == null || moq < 0) {
            throw new BusinessException(ResultCode.PARAM_RANGE_ERROR, "最小起订量不能为负数");
        }
        if (leadTime == null || leadTime < 0) {
            throw new BusinessException(ResultCode.PARAM_RANGE_ERROR, "交付周期不能为负数");
        }
    }

    /**
     * 校验同配件供应商、同价格类型下报价日期区间是否重叠
     *
     * @param psId           配件供应商关联主键
     * @param priceType      价格类型
     * @param effectDate     生效日期
     * @param expireDate     失效日期，可为空
     * @param excludePriceId 修改场景下需排除的自身报价ID，新增时传 null
     */
    private void checkOverlap(Long psId, String priceType,
            LocalDate effectDate, LocalDate expireDate, Long excludePriceId) {
        long overlap = partSupplierPriceMapper.countOverlap(
                psId, priceType, effectDate, expireDate, excludePriceId);
        if (overlap > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE,
                    "该供应商在此时间区间内已存在同类型报价，请先调整生效/失效日期");
        }
    }
}
