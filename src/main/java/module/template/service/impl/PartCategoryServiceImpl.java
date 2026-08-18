package module.template.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.template.dto.PartCategorySaveDTO;
import module.template.entity.PartCategory;
import module.template.mapper.PartCategoryMapper;
import module.template.service.PartCategoryService;
import module.template.vo.PartCategoryDetailVO;
import module.template.vo.PartCategoryTreeVO;

@RequiredArgsConstructor
@Service
public class PartCategoryServiceImpl implements PartCategoryService {

    private final PartCategoryMapper partCategoryMapper;

    @Override
    public List<PartCategoryTreeVO> queryCategoryTree() {
        QueryWrapper<PartCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<PartCategory> partCategorieList = partCategoryMapper.selectList(queryWrapper);

        List<PartCategoryTreeVO> voList = BeanUtil.copyToList(partCategorieList, PartCategoryTreeVO.class);
        Map<Long, PartCategoryTreeVO> nodeMap = voList.stream()
                .collect(Collectors.toMap(PartCategoryTreeVO::getCatId, Function.identity()));

        List<PartCategoryTreeVO> rootList = new ArrayList<>();

        for (PartCategoryTreeVO dto : voList) {
            Long parentId = dto.getParentCatId();
            if (parentId == 0L) {
                rootList.add(dto);
            } else {
                PartCategoryTreeVO parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    if (parentNode.getChildren() == null) {
                        parentNode.setChildren(new ArrayList<>());
                    }
                    parentNode.getChildren().add(dto);
                }
            }
        }
        return rootList;
    }

    @Override
    public PartCategoryDetailVO queryCategoryDetail(Long catId) {

        if(catId == null){
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        PartCategory partCategory = partCategoryMapper.selectById(catId);
        if(partCategory == null){
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }
        PartCategoryDetailVO partCategoryDetailVO = new PartCategoryDetailVO();
        BeanUtil.copyProperties(partCategory, partCategoryDetailVO);
        return partCategoryDetailVO;

    }

    @Override
    public void addCategory(PartCategorySaveDTO partCategorySaveDTO) {
        if (partCategorySaveDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        String catName = partCategorySaveDTO.getCatName();
        long count = partCategoryMapper.countByCatName(catName);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, catName + "已存在");
        }

        PartCategory partCategory = new PartCategory();
        BeanUtil.copyProperties(partCategorySaveDTO, partCategory);

        partCategoryMapper.insert(partCategory);
    }

    @Override
    public void updateCategory(PartCategorySaveDTO partCategorySaveDTO) {
        if (partCategorySaveDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);
        }

        String catName = partCategorySaveDTO.getCatName();
        QueryWrapper<PartCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_name", catName);
        queryWrapper.ne("cat_id", partCategorySaveDTO.getCatId());
        long count = partCategoryMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_DUPLICATE, "模板名：" + catName + "已存在");
        }

        PartCategory partCategory = new PartCategory();
        BeanUtil.copyProperties(partCategorySaveDTO, partCategory);
        partCategoryMapper.updateById(partCategory);
    }

    @Override
    public void deleteCategory(Long catId) {

        if (catId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL);

        }
        QueryWrapper<PartCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_id", catId);
        long count = partCategoryMapper.selectCount(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "分类不存在");
        }
        partCategoryMapper.deleteById(catId);
    }

}
