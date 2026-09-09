package module.part.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.part.dto.PartParamValueSaveDTO;
import module.part.dto.PartParamValueUpdateDTO;
import module.part.entity.PartParamValue;
import module.part.mapper.PartParamValueMapper;
import module.part.service.PartParamValueService;

@RequiredArgsConstructor
@Service
public class PartParamValueServiceImpl implements PartParamValueService {

    private final PartParamValueMapper partParamValueMapper;

    @Override
    public void addPartParamValue(PartParamValueSaveDTO partParamValueSaveDTO) {
        if (partParamValueSaveDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "配件参数值保存DTO不能为空");
        }

        LambdaQueryWrapper<PartParamValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(PartParamValue::getPartId, partParamValueSaveDTO.getPartId())
                .eq(PartParamValue::getFieldId, partParamValueSaveDTO.getFieldId());

        long count = partParamValueMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "配件参数值已存在");
        }

        PartParamValue partParamValue = new PartParamValue();
        BeanUtil.copyProperties(partParamValueSaveDTO, partParamValue);
        int rows = partParamValueMapper.insert(partParamValue);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.ERROR, "新增配件参数值失败");
        }

    }


    @Override
    public void updatePartParamValue(PartParamValueUpdateDTO partParamValueUpdateDTO) {
        if(partParamValueUpdateDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "配件参数值更新DTO不能为空");
        }

        PartParamValue partParamValue = partParamValueMapper.selectById(partParamValueUpdateDTO.getParamValId());
        if(partParamValue == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件参数值不存在");
        }

        BeanUtil.copyProperties(partParamValueUpdateDTO, partParamValue);
        int rows = partParamValueMapper.updateById(partParamValue);
        if(rows <= 0) {
            throw new BusinessException(ResultCode.ERROR, "更新配件参数值失败");
        }
    }


    @Override
    public void deletePartParamValue(Long paramValId) {
        if(paramValId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "配件参数值ID不能为空");
        }

        PartParamValue partParamValue = partParamValueMapper.selectById(paramValId);
        if(partParamValue == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件参数值不存在");
        }

        int rows = partParamValueMapper.deleteById(paramValId);
        if(rows <= 0) {
            throw new BusinessException(ResultCode.ERROR, "删除配件参数值失败");
        }
    }

}
