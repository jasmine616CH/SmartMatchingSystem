package module.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.system.dto.DictItemSaveDTO;
import module.system.dto.DictItemUpdateDTO;
import module.system.entity.SysDictItem;
import module.system.entity.SysDictType;
import module.system.mapper.SysDictItemMapper;
import module.system.mapper.SysDictTypeMapper;
import module.system.service.SysDictItemService;
import module.system.vo.DictItemVO;

/**
 * 字典项业务实现类
 */
@RequiredArgsConstructor
@Service
public class SysDictItemServiceImpl implements SysDictItemService {

    private final SysDictItemMapper sysDictItemMapper;

    private final SysDictTypeMapper sysDictTypeMapper;

    /** 默认启用 */
    private static final int STATUS_ENABLED = 1;

    /**
     * 查询某字典下的全部字典项
     * <p>管理页面需要看到停用项，故不按 status 过滤（对外取值接口会过滤）。
     */
    @Override
    public List<DictItemVO> queryItemList(Long dictTypeId) {
        if (dictTypeId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "dictTypeId 不能为空");
        }
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getDictTypeId, dictTypeId)
                .orderByAsc(SysDictItem::getSort)
                .orderByAsc(SysDictItem::getDictItemId);
        return sysDictItemMapper.selectList(wrapper).stream()
                .map(e -> BeanUtil.toBean(e, DictItemVO.class))
                .toList();
    }

    /**
     * 新增字典项
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addItem(DictItemSaveDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        checkDictTypeExists(dto.getDictTypeId());
        checkValueDuplicate(dto.getDictTypeId(), dto.getValue(), null);

        SysDictItem entity = BeanUtil.toBean(dto, SysDictItem.class);
        entity.setSort(dto.getSort() == null ? 0 : dto.getSort());
        entity.setStatus(dto.getStatus() == null ? STATUS_ENABLED : dto.getStatus());
        if (sysDictItemMapper.insert(entity) == 0) {
            throw new BusinessException(ResultCode.ERROR, "新增字典项失败");
        }
    }

    /**
     * 修改字典项
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(DictItemUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        if (dto.getDictItemId() == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "dictItemId 不能为空");
        }
        SysDictItem db = sysDictItemMapper.selectById(dto.getDictItemId());
        if (db == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字典项不存在");
        }
        checkDictTypeExists(dto.getDictTypeId());
        checkValueDuplicate(dto.getDictTypeId(), dto.getValue(), dto.getDictItemId());

        SysDictItem entity = BeanUtil.toBean(dto, SysDictItem.class);
        if (sysDictItemMapper.updateById(entity) == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字典项不存在");
        }
    }

    /**
     * 删除字典项
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long dictItemId) {
        if (dictItemId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "dictItemId 不能为空");
        }
        if (sysDictItemMapper.selectById(dictItemId) == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字典项不存在");
        }
        sysDictItemMapper.deleteById(dictItemId);
    }

    // ==================== 私有方法 ====================

    private void checkDictTypeExists(Long dictTypeId) {
        if (dictTypeId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "dictTypeId 不能为空");
        }
        SysDictType type = sysDictTypeMapper.selectById(dictTypeId);
        if (type == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "字典类型不存在");
        }
    }

    /**
     * 同一字典下 value 唯一（与库中 uk_dict_type_value 对应，这里给出可读报错）
     *
     * @param excludeItemId 修改场景下排除自身
     */
    private void checkValueDuplicate(Long dictTypeId, String value, Long excludeItemId) {
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getDictTypeId, dictTypeId)
                .eq(SysDictItem::getValue, value);
        if (excludeItemId != null) {
            wrapper.ne(SysDictItem::getDictItemId, excludeItemId);
        }
        if (sysDictItemMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE,
                    "该字典下已存在取值【" + value + "】");
        }
    }
}
