package module.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.system.dto.DictTypeQueryDTO;
import module.system.dto.DictTypeSaveDTO;
import module.system.dto.DictTypeUpdateDTO;
import module.system.entity.SysDictItem;
import module.system.entity.SysDictType;
import module.system.mapper.SysDictItemMapper;
import module.system.mapper.SysDictTypeMapper;
import module.system.service.SysDictTypeService;
import module.system.vo.DictDetailVO;
import module.system.vo.DictOptionVO;
import module.system.vo.DictTypeVO;

@RequiredArgsConstructor
@Service
public class SysDictTypeServiceImpl implements SysDictTypeService {

    private final SysDictTypeMapper sysDictTypeMapper;
    private final SysDictItemMapper sysDictItemMapper;

    /** 字典大类编码：SYS / PARAM / ACCESSORY / RULE */
    private static final List<String> VALID_CATEGORIES = List.of("SYS", "PARAM", "ACCESSORY", "RULE");

    /** 默认启用 */
    private static final int STATUS_ENABLED = 1;

    @Override
    public List<DictOptionVO> getEnableDictItemByDictCode(String dictCode) {
        // 1. 根据dictCode查询启用的字典类型，只查dictTypeId，减少字段开销
        LambdaQueryWrapper<SysDictType> typeWrapper = Wrappers.lambdaQuery();
        typeWrapper.select(SysDictType::getDictTypeId)
                .eq(SysDictType::getDictCode, dictCode)
                .eq(SysDictType::getStatus, 1);
        SysDictType sysDictType = sysDictTypeMapper.selectOne(typeWrapper);
        if (sysDictType == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "该字典编码不存在或已停用");
        }
        Long dictTypeId = sysDictType.getDictTypeId();

        // 2. 查询启用字典项，只需要label、value，按sort升序，同sort按label兜底排序
        LambdaQueryWrapper<SysDictItem> itemWrapper = Wrappers.lambdaQuery();
        itemWrapper.select(SysDictItem::getValue, SysDictItem::getLabel, SysDictItem::getSort)
                .eq(SysDictItem::getDictTypeId, dictTypeId)
                .eq(SysDictItem::getStatus, 1)
                .orderByAsc(SysDictItem::getSort)
                .orderByAsc(SysDictItem::getLabel);

        List<SysDictItem> dictItemList = sysDictItemMapper.selectList(itemWrapper);
        // 转为前端下拉VO
        return BeanUtil.copyToList(dictItemList, DictOptionVO.class);
    }

    /**
     * 按字典编码取启用的字典内容
     * <p>复用 getEnableDictItemByDictCode 的取值口径（只取启用项、按 sort 升序），
     * 额外补上 dictName，省得前端再查一次字典类型。
     */
    @Override
    public DictDetailVO getEnabledDictDetail(String dictCode) {
        List<DictOptionVO> items = getEnableDictItemByDictCode(dictCode);

        LambdaQueryWrapper<SysDictType> wrapper = Wrappers.lambdaQuery();
        wrapper.select(SysDictType::getDictCode, SysDictType::getDictName)
                .eq(SysDictType::getDictCode, dictCode);
        SysDictType type = sysDictTypeMapper.selectOne(wrapper);

        DictDetailVO vo = new DictDetailVO();
        vo.setDictCode(dictCode);
        vo.setDictName(type == null ? null : type.getDictName());
        vo.setItems(items);
        return vo;
    }

    // ==================== 字典管理页面 ====================

    /**
     * 分页查询字典类型
     * <p>管理页面需要看到停用项，故 status 不传时不过滤。
     */
    @Override
    public Page<DictTypeVO> queryDictTypePage(DictTypeQueryDTO dto) {
        DictTypeQueryDTO query = dto == null ? new DictTypeQueryDTO() : dto;
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getStatus() != null, SysDictType::getStatus, query.getStatus())
                .eq(query.getDictCategory() != null && !query.getDictCategory().isEmpty(),
                        SysDictType::getDictCategory, query.getDictCategory())
                .like(query.getDictCode() != null && !query.getDictCode().isEmpty(),
                        SysDictType::getDictCode, query.getDictCode())
                .like(query.getDictName() != null && !query.getDictName().isEmpty(),
                        SysDictType::getDictName, query.getDictName())
                .orderByAsc(SysDictType::getDictCategory)
                .orderByAsc(SysDictType::getDictTypeId);

        Page<SysDictType> page = sysDictTypeMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);

        Page<DictTypeVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toVO).toList());
        return result;
    }

    /**
     * 查询字典类型详情
     */
    @Override
    public DictTypeVO queryDictTypeDetail(Long dictTypeId) {
        if (dictTypeId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "dictTypeId 不能为空");
        }
        SysDictType entity = sysDictTypeMapper.selectById(dictTypeId);
        if (entity == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字典类型不存在");
        }
        return toVO(entity);
    }

    /**
     * 新增字典类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addDictType(DictTypeSaveDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        checkCategoryValid(dto.getDictCategory());
        checkDictCodeDuplicate(dto.getDictCode(), null);

        SysDictType entity = BeanUtil.toBean(dto, SysDictType.class);
        entity.setStatus(dto.getStatus() == null ? STATUS_ENABLED : dto.getStatus());
        if (sysDictTypeMapper.insert(entity) == 0) {
            throw new BusinessException(ResultCode.ERROR, "新增字典类型失败");
        }
    }

    /**
     * 修改字典类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(DictTypeUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        if (dto.getDictTypeId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "dictTypeId 不能为空");
        }
        SysDictType db = sysDictTypeMapper.selectById(dto.getDictTypeId());
        if (db == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字典类型不存在");
        }
        checkCategoryValid(dto.getDictCategory());
        // 参数模板等业务是按 dictCode 反查字典的，改了会让既有引用失效
        if (!db.getDictCode().equals(dto.getDictCode())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "字典编码不允许修改，参数模板可能已按该编码关联本字典");
        }

        SysDictType entity = BeanUtil.toBean(dto, SysDictType.class);
        if (sysDictTypeMapper.updateById(entity) == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字典类型不存在");
        }
    }

    /**
     * 删除字典类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long dictTypeId) {
        if (dictTypeId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "dictTypeId 不能为空");
        }
        if (sysDictTypeMapper.selectById(dictTypeId) == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字典类型不存在");
        }
        // 字典项依附于字典类型，先删干净再删主体，避免留下孤儿数据
        LambdaQueryWrapper<SysDictItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(SysDictItem::getDictTypeId, dictTypeId);
        long itemCount = sysDictItemMapper.selectCount(itemWrapper);
        if (itemCount > 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该字典下还有 " + itemCount + " 个字典项，请先删除字典项");
        }
        sysDictTypeMapper.deleteById(dictTypeId);
    }

    // ==================== 私有方法 ====================

    private DictTypeVO toVO(SysDictType entity) {
        DictTypeVO vo = BeanUtil.toBean(entity, DictTypeVO.class);
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getDictTypeId, entity.getDictTypeId());
        vo.setItemCount(Math.toIntExact(sysDictItemMapper.selectCount(wrapper)));
        return vo;
    }

    private void checkCategoryValid(String category) {
        if (category == null || !VALID_CATEGORIES.contains(category)) {
            throw new BusinessException(ResultCode.PARAM_VALUE_INVALID,
                    "字典大类只能是 " + String.join(" / ", VALID_CATEGORIES));
        }
    }

    private void checkDictCodeDuplicate(String dictCode, Long excludeId) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictCode, dictCode);
        if (excludeId != null) {
            wrapper.ne(SysDictType::getDictTypeId, excludeId);
        }
        if (sysDictTypeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE,
                    "字典编码【" + dictCode + "】已存在");
        }
    }
}
