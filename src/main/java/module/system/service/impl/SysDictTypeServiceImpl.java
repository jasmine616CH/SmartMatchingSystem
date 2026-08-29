package module.system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.system.entity.SysDictItem;
import module.system.entity.SysDictType;
import module.system.mapper.SysDictItemMapper;
import module.system.mapper.SysDictTypeMapper;
import module.system.service.SysDictTypeService;
import module.system.vo.DictOptionVO;

@RequiredArgsConstructor
@Service
public class SysDictTypeServiceImpl implements SysDictTypeService {

    private final SysDictTypeMapper sysDictTypeMapper;
    private final SysDictItemMapper sysDictItemMapper;

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
        itemWrapper.select(SysDictItem::getValue, SysDictItem::getLabel)
                .eq(SysDictItem::getDictTypeId, dictTypeId)
                .eq(SysDictItem::getStatus, 1)
                .orderByAsc(SysDictItem::getSort)
                .orderByAsc(SysDictItem::getLabel);

        List<SysDictItem> dictItemList = sysDictItemMapper.selectList(itemWrapper);
        // 转为前端下拉VO
        return BeanUtil.copyToList(dictItemList, DictOptionVO.class);
    }

}
