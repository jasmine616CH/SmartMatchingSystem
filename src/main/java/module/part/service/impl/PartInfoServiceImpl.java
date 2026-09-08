package module.part.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.SecurityUtils;
import lombok.RequiredArgsConstructor;
import module.part.dto.PartInfoSaveDTO;
import module.part.dto.PartInfoUpdateDTO;
import module.part.entity.PartInfo;
import module.part.mapper.PartInfoMapper;
import module.part.service.PartInfoService;
import module.part.vo.PartInfoListVO;

@RequiredArgsConstructor
@Service
public class PartInfoServiceImpl implements PartInfoService {

    private final PartInfoMapper partInfoMapper;

    @Override
    public Page<PartInfoListVO> queryPartInfoList(Long catId, Integer pageNum, Integer pageSize) {
        if (catId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "catId不能为空");
        }
        Page<PartInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PartInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartInfo::getCatId, catId);
        Page<PartInfo> resultPage = partInfoMapper.selectPage(page, queryWrapper);

        List<PartInfoListVO> voList = resultPage.getRecords().stream()
                .map(entity -> BeanUtil.toBean(entity, PartInfoListVO.class))
                .toList();

        Page<PartInfoListVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize());
        voPage.setRecords(voList);
        voPage.setTotal(resultPage.getTotal());
        return voPage;
    }

    @Override
    public void addPartInfo(PartInfoSaveDTO partInfoSaveDTO) {
        if (partInfoSaveDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        PartInfo partInfo = BeanUtil.toBean(partInfoSaveDTO, PartInfo.class);
        partInfo.setMaintainUserId(SecurityUtils.getCurrentUserId());

        LambdaQueryWrapper<PartInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartInfo::getPartCode, partInfo.getPartCode());
        Long existingCount = partInfoMapper.selectCount(queryWrapper);
        if (existingCount > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "partCode已存在");
        }
        partInfoMapper.insert(partInfo);
    }

    @Override
    public void updatePartInfo(PartInfoUpdateDTO partInfoUpdateDTO) {
        if (partInfoUpdateDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        PartInfo partInfo = BeanUtil.toBean(partInfoUpdateDTO, PartInfo.class);
        partInfo.setMaintainUserId(SecurityUtils.getCurrentUserId());

        LambdaQueryWrapper<PartInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(PartInfo::getPartCode, partInfo.getPartCode())
                .ne(PartInfo::getCatId, partInfo.getCatId());
        Long existingCount = partInfoMapper.selectCount(queryWrapper);
        if (existingCount > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "partCode已存在");
        }
        int rows = partInfoMapper.updateById(partInfo);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件信息不存在");
        }
    }

    @Override
    public void deletePartInfo(Long partId) {
        if (partId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "partId不能为空");
        }
        int rows = partInfoMapper.deleteById(partId);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件信息不存在");
        }
    }

}
