package module.part.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import common.enums.BizType;
import common.enums.PublishStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.SecurityUtils;
import lombok.RequiredArgsConstructor;
import module.review.service.AuditTaskService;
import module.part.dto.PartInfoSaveDTO;
import module.part.dto.PartInfoUpdateDTO;
import module.part.entity.PartInfo;
import module.part.mapper.PartInfoMapper;
import module.part.service.PartInfoService;
import module.part.vo.PartInfoDetailVO;
import module.part.vo.PartInfoListVO;

@RequiredArgsConstructor
@Service
public class PartInfoServiceImpl implements PartInfoService {

    private final PartInfoMapper partInfoMapper;

    private final AuditTaskService auditTaskService;

    @Override
    public Page<PartInfoListVO> queryPartInfoList(Long catId, Integer pageNum, Integer pageSize) {
        if (catId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "catId不能为空");
        }
        Page<PartInfoListVO> page = new Page<>(pageNum, pageSize);
        Page<PartInfoListVO> result = partInfoMapper.selectPartPage(page, catId);

        // 发布状态中文由后端统一给出，前端不必自己映射
        result.getRecords().forEach(vo ->
                vo.setPublishingStatusName(PublishStatus.getNameByCode(vo.getPublishingStatus())));
        return result;
    }

    /**
     * 查询配件档案详情
     */
    @Override
    public PartInfoDetailVO queryPartInfoDetail(Long partId) {
        if (partId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "partId不能为空");
        }
        PartInfoDetailVO detail = partInfoMapper.selectPartDetail(partId);
        if (detail == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件信息不存在");
        }
        detail.setPublishingStatusName(PublishStatus.getNameByCode(detail.getPublishingStatus()));
        return detail;
    }

    @Override
    public void addPartInfo(PartInfoSaveDTO partInfoSaveDTO) {
        if (partInfoSaveDTO == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        PartInfo partInfo = BeanUtil.toBean(partInfoSaveDTO, PartInfo.class);
        partInfo.setMaintainUserId(SecurityUtils.getCurrentUserId());
        // 发布状态由审批流驱动，不接受前端传入；新建一律进草稿
        partInfo.setPublishingStatus(PublishStatus.DRAFT.getCode());
        partInfo.setAuditUserId(null);

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
        PartInfo db = partInfoMapper.selectById(partInfoUpdateDTO.getPartId());
        if (db == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件信息不存在");
        }
        checkEditable(db.getPublishingStatus());

        PartInfo partInfo = BeanUtil.toBean(partInfoUpdateDTO, PartInfo.class);
        partInfo.setMaintainUserId(SecurityUtils.getCurrentUserId());
        // 状态与审批人不允许通过修改接口变更：置 null 让 MP 的 NOT_NULL 策略跳过这两列
        partInfo.setPublishingStatus(null);
        partInfo.setAuditUserId(null);

        LambdaQueryWrapper<PartInfo> queryWrapper = new LambdaQueryWrapper<>();
        // 原实现这里比的是 cat_id（分类），应该比主键：否则「同一分类下改自己的编码」会误判重复，
        // 而真正的重复（同分类下已存在同编码）反而查不出来
        queryWrapper
                .eq(PartInfo::getPartCode, partInfo.getPartCode())
                .ne(PartInfo::getPartId, partInfo.getPartId());
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
        PartInfo db = partInfoMapper.selectById(partId);
        if (db == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件信息不存在");
        }
        // 仅草稿可删：待审核/已发布被删会让审批任务变成孤儿
        if (!PublishStatus.DRAFT.getCode().equals(db.getPublishingStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅草稿状态的配件允许删除，请先执行撤回操作");
        }
        int rows = partInfoMapper.deleteById(partId);
        if (rows == 0) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件信息不存在");
        }
    }


    /**
     * 提交审核：草稿 → 待审核
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAudit(Long partId) {
        PartInfo part = getPartOrThrow(partId);
        if (!PublishStatus.DRAFT.getCode().equals(part.getPublishingStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅草稿状态的配件允许提交审核");
        }
        // 先 CAS 业务行，失败即回滚，不会留下多余的待办任务
        updateStatusCas(partId, PublishStatus.DRAFT, PublishStatus.PENDING_AUDIT);
        auditTaskService.createPendingTask(BizType.PART_INFO, partId);
    }

    /**
     * 撤回：已发布 → 草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long partId) {
        PartInfo part = getPartOrThrow(partId);
        if (!PublishStatus.PUBLISHED.getCode().equals(part.getPublishingStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅已发布状态的配件允许撤回");
        }
        updateStatusCasAndAudit(partId, PublishStatus.PUBLISHED, PublishStatus.DRAFT, null);
        auditTaskService.revokeLatestApproved(BizType.PART_INFO, partId);
    }

    /**
     * 撤销申请：待审核 → 草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSubmit(Long partId) {
        PartInfo part = getPartOrThrow(partId);
        if (!PublishStatus.PENDING_AUDIT.getCode().equals(part.getPublishingStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅待审核状态的配件允许撤销申请");
        }
        updateStatusCas(partId, PublishStatus.PENDING_AUDIT, PublishStatus.DRAFT);
        auditTaskService.revokeLatestPending(BizType.PART_INFO, partId);
    }

    // ==================== 私有方法 ====================

    private PartInfo getPartOrThrow(Long partId) {
        if (partId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "partId不能为空");
        }
        PartInfo part = partInfoMapper.selectById(partId);
        if (part == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "配件信息不存在");
        }
        return part;
    }

    /**
     * 校验当前状态是否允许编辑
     */
    private void checkEditable(Integer status) {
        if (PublishStatus.PENDING_AUDIT.getCode().equals(status)) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "配件处于【待审核】状态，暂不支持编辑，请先等待审批或撤销申请");
        }
        if (PublishStatus.PUBLISHED.getCode().equals(status)) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "配件处于【已发布】状态，暂不支持编辑，请先执行撤回操作");
        }
    }

    /**
     * 带原状态条件地更新发布状态（状态机 CAS），不动审批人字段
     */
    private void updateStatusCas(Long partId, PublishStatus from, PublishStatus to) {
        updateStatusCasAndAudit(partId, from, to, null, false);
    }

    /**
     * 带原状态条件地更新发布状态，并同时清空审批人（撤回/驳回场景）
     */
    private void updateStatusCasAndAudit(Long partId, PublishStatus from, PublishStatus to,
            Long auditUserId) {
        updateStatusCasAndAudit(partId, from, to, auditUserId, true);
    }

    private void updateStatusCasAndAudit(Long partId, PublishStatus from, PublishStatus to,
            Long auditUserId, boolean touchAuditUser) {
        LambdaUpdateWrapper<PartInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PartInfo::getPartId, partId)
                .eq(PartInfo::getPublishingStatus, from.getCode())
                .set(PartInfo::getPublishingStatus, to.getCode());
        if (touchAuditUser) {
            wrapper.set(PartInfo::getAuditUserId, auditUserId);
        }
        if (partInfoMapper.update(null, wrapper) == 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "配件状态已变更，请刷新后重试");
        }
    }
}
