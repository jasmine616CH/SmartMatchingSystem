package module.category.spi;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import cn.hutool.core.bean.BeanUtil;
import common.enums.BizType;
import common.enums.PublishStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.category.entity.PartCategory;
import module.category.mapper.PartCategoryMapper;
import module.category.vo.PartCategoryDetailVO;
import module.review.spi.BizAuditHandler;
import module.review.vo.AuditBizSummaryVO;

/**
 * 配件分类的审批扩展点
 * <p>
 * 只依赖 Mapper，不依赖本模块 Service（循环依赖约束见 BizAuditHandler javadoc）。
 * <p>
 * 分类是树形结构，因此比另外两类多一条<b>父子联动规则</b>：
 * 提交审核时父分类必须已发布，否则会出现「子已发布、父还是草稿」的悬空状态。
 */
@Component
@RequiredArgsConstructor
public class PartCategoryBizAuditHandler implements BizAuditHandler {

    private final PartCategoryMapper partCategoryMapper;

    /** 根分类的 parent_cat_id 约定为 0 */
    private static final long ROOT_PARENT_ID = 0L;

    @Override
    public BizType bizType() {
        return BizType.PART_CATEGORY;
    }

    /**
     * 提交审核前置校验：分类存在，且<b>父分类已发布</b>（根分类除外）
     */
    @Override
    public void validateSubmit(Long bizId) {
        PartCategory category = partCategoryMapper.selectById(bizId);
        if (category == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件分类不存在");
        }
        checkParentPublished(category);
    }

    @Override
    public AuditBizSummaryVO loadSummary(Long bizId) {
        PartCategory category = partCategoryMapper.selectById(bizId);
        if (category == null) {
            return null;
        }
        AuditBizSummaryVO summary = new AuditBizSummaryVO();
        summary.setBizName(category.getCatName());
        summary.setBizCode(category.getCatCode());
        summary.setBizStatus(category.getStatus());
        summary.setBizStatusName(PublishStatus.getNameByCode(category.getStatus()));
        return summary;
    }

    @Override
    public Object loadDetail(Long bizId) {
        PartCategory category = partCategoryMapper.selectById(bizId);
        if (category == null) {
            return null;
        }
        return BeanUtil.toBean(category, PartCategoryDetailVO.class);
    }

    /**
     * 审批通过：待审核 → 已发布，并记录审批人
     */
    @Override
    public int markApproved(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<PartCategory> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PartCategory::getCatId, bizId)
                .eq(PartCategory::getStatus, PublishStatus.PENDING_AUDIT.getCode())
                .set(PartCategory::getStatus, PublishStatus.PUBLISHED.getCode())
                .set(PartCategory::getAuditUserId, auditUserId);
        return partCategoryMapper.update(null, wrapper);
    }

    /**
     * 审批驳回：待审核 → 草稿，并清空审批人
     */
    @Override
    public int markRejected(Long bizId, Long auditUserId) {
        LambdaUpdateWrapper<PartCategory> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PartCategory::getCatId, bizId)
                .eq(PartCategory::getStatus, PublishStatus.PENDING_AUDIT.getCode())
                .set(PartCategory::getStatus, PublishStatus.DRAFT.getCode())
                .set(PartCategory::getAuditUserId, null);
        return partCategoryMapper.update(null, wrapper);
    }

    /**
     * 父分类必须已发布；根分类没有父，直接放行。
     * <p>
     * 不要求子分类逐级发布会导致树出现断裂：一个已发布的三级类别挂在草稿的二级子系统下，
     * 按子系统找分类的查询就会漏掉它。
     */
    private void checkParentPublished(PartCategory category) {
        Long parentId = category.getParentCatId();
        if (parentId == null || parentId == ROOT_PARENT_ID) {
            return;
        }
        PartCategory parent = partCategoryMapper.selectById(parentId);
        if (parent == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "上级分类不存在：" + parentId);
        }
        if (!PublishStatus.PUBLISHED.getCode().equals(parent.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "上级分类【" + parent.getCatName() + "】未发布，请先发布上级分类");
        }
    }
}
