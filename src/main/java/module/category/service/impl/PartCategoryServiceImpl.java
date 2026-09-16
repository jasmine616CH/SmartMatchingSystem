package module.category.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.enums.BizType;
import common.enums.PublishStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.category.dto.PartCategorySaveDTO;
import module.category.entity.PartCategory;
import module.category.mapper.PartCategoryMapper;
import module.part.entity.PartInfo;
import module.part.mapper.PartInfoMapper;
import module.review.service.AuditTaskService;
import module.template.entity.ParamTemplate;
import module.template.mapper.ParamTemplateMapper;
import module.category.service.PartCategoryService;
import module.category.vo.PartCategoryDetailVO;
import module.category.vo.PartCategoryTreeVO;

@RequiredArgsConstructor
@Service
public class PartCategoryServiceImpl implements PartCategoryService {

    private final PartCategoryMapper partCategoryMapper;

    private final PartInfoMapper partInfoMapper;

    private final ParamTemplateMapper paramTemplateMapper;

    private final AuditTaskService auditTaskService;

    /** 根分类的 parent_cat_id 约定为 0 */
    private static final long ROOT_PARENT_ID = 0L;

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

        // 发布状态由审批流驱动，新建一律进草稿
        partCategory.setStatus(PublishStatus.DRAFT.getCode());
        partCategory.setAuditUserId(null);
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
        // 状态与审批人不允许通过修改接口变更
        partCategory.setStatus(null);
        partCategory.setAuditUserId(null);
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
        // 有子分类、或已被配件/模板引用时不允许删除，否则会留下悬空引用
        QueryWrapper<PartCategory> childWrapper = new QueryWrapper<>();
        childWrapper.eq("parent_cat_id", catId);
        if (partCategoryMapper.selectCount(childWrapper) > 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该分类下还有子分类，请先删除子分类");
        }
        QueryWrapper<PartInfo> partWrapper = new QueryWrapper<>();
        partWrapper.eq("cat_id", catId);
        if (partInfoMapper.selectCount(partWrapper) > 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该分类下已挂配件，不允许删除");
        }
        QueryWrapper<ParamTemplate> tplWrapper = new QueryWrapper<>();
        tplWrapper.eq("cat_id", catId);
        if (paramTemplateMapper.selectCount(tplWrapper) > 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该分类下已绑定参数模板，不允许删除");
        }
        partCategoryMapper.deleteById(catId);
    }


    /**
     * 提交审核：草稿 → 待审核
     * <p>
     * 分类是树，父分类未发布时不允许子分类提交——否则会出现「子已发布、父还是草稿」的悬空状态。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAudit(Long catId) {
        PartCategory category = getCategoryOrThrow(catId);
        if (!PublishStatus.DRAFT.getCode().equals(category.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅草稿状态的分类允许提交审核");
        }
        if (category.getParentCatId() != null && category.getParentCatId() != ROOT_PARENT_ID) {
            PartCategory parent = partCategoryMapper.selectById(category.getParentCatId());
            if (parent == null) {
                throw new BusinessException(ResultCode.DATA_NOT_EXIST, "上级分类不存在");
            }
            if (!PublishStatus.PUBLISHED.getCode().equals(parent.getStatus())) {
                throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                        "上级分类【" + parent.getCatName() + "】未发布，请先发布上级分类");
            }
        }
        updateStatusCas(catId, PublishStatus.DRAFT, PublishStatus.PENDING_AUDIT);
        auditTaskService.createPendingTask(BizType.PART_CATEGORY, catId);
    }

    /**
     * 撤回：已发布 → 草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long catId) {
        PartCategory category = getCategoryOrThrow(catId);
        if (!PublishStatus.PUBLISHED.getCode().equals(category.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅已发布状态的分类允许撤回");
        }
        updateStatusCasAndAudit(catId, PublishStatus.PUBLISHED, PublishStatus.DRAFT, null);
        auditTaskService.revokeLatestApproved(BizType.PART_CATEGORY, catId);
    }

    /**
     * 撤销申请：待审核 → 草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSubmit(Long catId) {
        PartCategory category = getCategoryOrThrow(catId);
        if (!PublishStatus.PENDING_AUDIT.getCode().equals(category.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅待审核状态的分类允许撤销申请");
        }
        updateStatusCas(catId, PublishStatus.PENDING_AUDIT, PublishStatus.DRAFT);
        auditTaskService.revokeLatestPending(BizType.PART_CATEGORY, catId);
    }

    // ==================== 私有方法 ====================

    private PartCategory getCategoryOrThrow(Long catId) {
        if (catId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "catId 不能为空");
        }
        PartCategory category = partCategoryMapper.selectById(catId);
        if (category == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "分类不存在");
        }
        return category;
    }

    /**
     * 带原状态条件地更新分类状态（状态机 CAS），不动审批人字段
     */
    private void updateStatusCas(Long catId, PublishStatus from, PublishStatus to) {
        updateStatusCasAndAudit(catId, from, to, null, false);
    }

    /**
     * 带原状态条件地更新分类状态，并同时清空审批人（撤回场景）
     */
    private void updateStatusCasAndAudit(Long catId, PublishStatus from, PublishStatus to,
            Long auditUserId) {
        updateStatusCasAndAudit(catId, from, to, auditUserId, true);
    }

    private void updateStatusCasAndAudit(Long catId, PublishStatus from, PublishStatus to,
            Long auditUserId, boolean touchAuditUser) {
        LambdaUpdateWrapper<PartCategory> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PartCategory::getCatId, catId)
                .eq(PartCategory::getStatus, from.getCode())
                .set(PartCategory::getStatus, to.getCode());
        if (touchAuditUser) {
            wrapper.set(PartCategory::getAuditUserId, auditUserId);
        }
        if (partCategoryMapper.update(null, wrapper) == 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "分类状态已变更，请刷新后重试");
        }
    }
}
