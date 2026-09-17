package module.supplier.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import common.enums.BizType;
import common.enums.SupplierQualificationStatus;
import common.enums.SupplierStatus;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.review.service.AuditTaskService;
import module.supplier.dto.SupplierContactSaveDTO;
import module.supplier.dto.SupplierContactUpdateDTO;
import module.supplier.dto.SupplierQueryDTO;
import module.supplier.dto.SupplierSaveDTO;
import module.supplier.dto.SupplierUpdateDTO;
import module.supplier.entity.PartSupplier;
import module.supplier.entity.Supplier;
import module.supplier.entity.SupplierContact;
import module.supplier.entity.SupplierQualification;
import module.supplier.mapper.PartSupplierMapper;
import module.supplier.mapper.SupplierContactMapper;
import module.supplier.mapper.SupplierMapper;
import module.supplier.mapper.SupplierQualificationMapper;
import module.supplier.service.SupplierService;
import module.supplier.vo.SupplierContactVO;
import module.supplier.vo.SupplierDetailVO;
import module.supplier.vo.SupplierListVO;

/**
 * 供应商主体业务实现类
 * 实现供应商的增删改查与审核流转
 */
@RequiredArgsConstructor
@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;

    private final SupplierContactMapper supplierContactMapper;

    private final SupplierQualificationMapper supplierQualificationMapper;

    private final PartSupplierMapper partSupplierMapper;

    private final AuditTaskService auditTaskService;

    /**
     * 分页查询供应商列表
     */
    @Override
    public Page<SupplierListVO> querySupplierList(SupplierQueryDTO dto) {
        SupplierQueryDTO query = dto == null ? new SupplierQueryDTO() : dto;

        Page<SupplierListVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<SupplierListVO> result = supplierMapper.selectSupplierPage(page, query);

        // 状态中文描述由枚举统一给出，避免前端硬编码
        result.getRecords().forEach(vo -> vo.setStatusName(SupplierStatus.getNameByCode(vo.getStatus())));
        return result;
    }

    /**
     * 查询供应商详情（含联系人列表）
     */
    @Override
    public SupplierDetailVO querySupplierDetail(Long supplierId) {
        if (supplierId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "supplierId 不能为空");
        }
        Supplier supplier = supplierMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }

        SupplierDetailVO vo = BeanUtil.toBean(supplier, SupplierDetailVO.class);
        vo.setStatusName(SupplierStatus.getNameByCode(supplier.getStatus()));
        vo.setContacts(listContacts(supplierId));
        return vo;
    }

    /**
     * 新增供应商（同时写入联系人），初始状态为草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSupplier(SupplierSaveDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        if (dto.getContacts() == null || dto.getContacts().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "至少填写一个联系人");
        }

        String creditCode = normalizeNullable(dto.getCreditCode());
        if (creditCode != null) {
            LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Supplier::getCreditCode, creditCode);
            if (supplierMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResultCode.DATA_DUPLICATE,
                        "统一社会信用代码【" + creditCode + "】已存在");
            }
        }

        Supplier supplier = BeanUtil.toBean(dto, Supplier.class);
        supplier.setCreditCode(creditCode);
        // 新建一律进草稿，提交审核后才会流转
        supplier.setStatus(SupplierStatus.DRAFT.getCode());
        // 草稿状态没有审批人
        supplier.setAuditUserId(null);

        int rows = supplierMapper.insert(supplier);
        if (rows == 0) {
            throw new BusinessException(ResultCode.ERROR, "新增供应商失败");
        }

        insertContacts(supplier.getSupplierId(), dto.getContacts());
    }

    /**
     * 修改供应商（联系人整体替换）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSupplier(SupplierUpdateDTO dto) {
        if (dto == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "数据为空");
        }
        Long supplierId = dto.getSupplierId();
        if (supplierId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "supplierId 不能为空");
        }
        Supplier dbSupplier = supplierMapper.selectById(supplierId);
        if (dbSupplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }
        checkEditable(dbSupplier.getStatus());

        String creditCode = normalizeNullable(dto.getCreditCode());
        if (creditCode != null) {
            LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Supplier::getCreditCode, creditCode)
                    .ne(Supplier::getSupplierId, supplierId);
            if (supplierMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResultCode.DATA_DUPLICATE,
                        "统一社会信用代码【" + creditCode + "】已存在");
            }
        }

        Supplier supplier = BeanUtil.toBean(dto, Supplier.class);
        supplier.setCreditCode(creditCode);
        // status 与 auditUserId 不允许通过修改接口变更：草稿状态两者都保持原值
        supplier.setStatus(null);
        supplier.setAuditUserId(null);

        int rows = supplierMapper.updateById(supplier);
        if (rows == 0) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }

        // 联系人整体替换：传了才动，不传表示只改主体信息
        List<SupplierContactUpdateDTO> contacts = dto.getContacts();
        if (contacts != null) {
            replaceContacts(supplierId, contacts);
        }
    }

    /**
     * 删除供应商（仅草稿状态可删）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSupplier(Long supplierId) {
        if (supplierId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "supplierId 不能为空");
        }
        Supplier supplier = supplierMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }
        // 仅草稿可删：待审核/已发布的记录若被删除，会让审批任务变成孤儿
        if (!SupplierStatus.DRAFT.getCode().equals(supplier.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅草稿状态的供应商允许删除，请先执行撤回操作");
        }
        // 已关联配件的供应商不允许删除
        LambdaQueryWrapper<PartSupplier> partWrapper = new LambdaQueryWrapper<>();
        partWrapper.eq(PartSupplier::getSupplierId, supplierId);
        if (partSupplierMapper.selectCount(partWrapper) > 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该供应商已关联配件，不允许删除");
        }
        // 存在待审核或有效的资质时不允许删除
        LambdaQueryWrapper<SupplierQualification> qfWrapper = new LambdaQueryWrapper<>();
        qfWrapper.eq(SupplierQualification::getSupplierId, supplierId)
                .in(SupplierQualification::getStatus,
                        SupplierQualificationStatus.PENDING_AUDIT.getCode(),
                        SupplierQualificationStatus.VALID.getCode());
        if (supplierQualificationMapper.selectCount(qfWrapper) > 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该供应商存在待审核或有效的资质，请先处理后再删除");
        }
        // 兜底：理论上前面已排除，但防止运维直连改库造成孤儿任务
        if (auditTaskService.countPending(BizType.SUPPLIER, supplierId) > 0) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "该供应商存在待审核任务，请先撤回后再删除");
        }

        // 联系人无独立生命周期，随主体一并清理
        LambdaQueryWrapper<SupplierContact> contactWrapper = new LambdaQueryWrapper<>();
        contactWrapper.eq(SupplierContact::getSupplierId, supplierId);
        supplierContactMapper.delete(contactWrapper);

        int rows = supplierMapper.deleteById(supplierId);
        if (rows == 0) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }
    }

    /**
     * 提交审核：草稿 → 待审核
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAudit(Long supplierId) {
        if (supplierId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "supplierId 不能为空");
        }
        Supplier supplier = supplierMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }
        if (!SupplierStatus.DRAFT.getCode().equals(supplier.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅草稿状态的供应商允许提交审核");
        }
        if (!StringUtils.hasText(supplier.getCreditCode())) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL,
                    "供应商缺少统一社会信用代码，无法提交审核");
        }
        LambdaQueryWrapper<SupplierContact> contactWrapper = new LambdaQueryWrapper<>();
        contactWrapper.eq(SupplierContact::getSupplierId, supplierId);
        if (supplierContactMapper.selectCount(contactWrapper) == 0) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL,
                    "供应商缺少联系人，无法提交审核");
        }

        // 先 CAS 业务行：失败即整体回滚，不会留下多余的待办任务
        updateStatusCas(supplierId, SupplierStatus.DRAFT, SupplierStatus.PENDING_AUDIT);
        auditTaskService.createPendingTask(BizType.SUPPLIER, supplierId);
    }

    /**
     * 撤回：已发布 → 草稿
     * <p>
     * 特意落到草稿而非待审核：撤回不是重新提交，落待审核却不建任务会让记录卡死
     * （审批人看不到，revoke 又只允许从已发布触发，等于没有出口）。
     * 落到草稿可维持不变量「状态为待审核 ⟺ 存在一条待审核任务」。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revoke(Long supplierId) {
        if (supplierId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "supplierId 不能为空");
        }
        Supplier supplier = supplierMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }
        if (!SupplierStatus.PUBLISHED.getCode().equals(supplier.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅已发布状态的供应商允许撤回");
        }
        // 撤回后清空审批人：草稿状态不该留着上一个审批人
        updateStatusCasAndAudit(supplierId, SupplierStatus.PUBLISHED, SupplierStatus.DRAFT, null);
        auditTaskService.revokeLatestApproved(BizType.SUPPLIER, supplierId);
    }

    /**
     * 撤销申请：待审核 → 草稿
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSubmit(Long supplierId) {
        if (supplierId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "supplierId 不能为空");
        }
        Supplier supplier = supplierMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }
        if (!SupplierStatus.PENDING_AUDIT.getCode().equals(supplier.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "仅待审核状态的供应商允许撤销申请");
        }
        updateStatusCas(supplierId, SupplierStatus.PENDING_AUDIT, SupplierStatus.DRAFT);
        auditTaskService.revokeLatestPending(BizType.SUPPLIER, supplierId);
    }

    // ==================== 包内可见：供 SPI 处理器复用的状态读写 ====================

    /**
     * 带原状态条件地更新供应商状态（状态机 CAS），不动审批人字段
     *
     * @param supplierId 供应商主键ID
     * @param from       期望的原状态
     * @param to         目标状态
     * @return 受影响行数，0 表示状态已被他人变更
     */
    int updateStatusCas(Long supplierId, SupplierStatus from, SupplierStatus to) {
        return updateStatusCasAndAudit(supplierId, from, to, null, false);
    }

    /**
     * 带原状态条件地更新供应商状态，并同时写审批人（状态机 CAS）
     *
     * @param supplierId  供应商主键ID
     * @param from        期望的原状态
     * @param to          目标状态
     * @param auditUserId 审批人用户ID，传 null 表示清空（驳回/撤回场景）
     * @return 受影响行数，0 表示状态已被他人变更
     */
    int updateStatusCasAndAudit(Long supplierId, SupplierStatus from, SupplierStatus to, Long auditUserId) {
        return updateStatusCasAndAudit(supplierId, from, to, auditUserId, true);
    }

    /**
     * 状态机 CAS 的统一实现
     * <p>
     * 注意 {@code audit_user_id} 必须用 {@code .set(...)} 显式赋值：
     * 若改用实体 + updateById，NOT_NULL 策略会把 null 静默跳过，导致清空不掉审批人。
     *
     * @param touchAuditUser 是否同时写 audit_user_id（false 表示保持原值不变）
     */
    private int updateStatusCasAndAudit(Long supplierId, SupplierStatus from, SupplierStatus to,
            Long auditUserId, boolean touchAuditUser) {
        LambdaUpdateWrapper<Supplier> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Supplier::getSupplierId, supplierId)
                .eq(Supplier::getStatus, from.getCode())
                .set(Supplier::getStatus, to.getCode());
        if (touchAuditUser) {
            wrapper.set(Supplier::getAuditUserId, auditUserId);
        }
        // 实体参数必须传 null：否则 MP 会把实体的非空字段（含主键）一并拼进 SET 子句
        return supplierMapper.update(null, wrapper);
    }

    /**
     * 查询供应商的全部联系人
     *
     * @param supplierId 供应商主键ID
     * @return 联系人列表
     */
    List<SupplierContactVO> listContacts(Long supplierId) {
        LambdaQueryWrapper<SupplierContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierContact::getSupplierId, supplierId)
                .orderByAsc(SupplierContact::getContactId);
        return supplierContactMapper.selectList(wrapper).stream()
                .map(c -> BeanUtil.toBean(c, SupplierContactVO.class))
                .toList();
    }

    // ==================== 私有方法 ====================

    /**
     * 校验当前状态是否允许编辑
     */
    private void checkEditable(Integer status) {
        if (SupplierStatus.PENDING_AUDIT.getCode().equals(status)) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "供应商处于【待审核】状态，暂不支持编辑，请先等待审批或撤销申请");
        }
        if (SupplierStatus.PUBLISHED.getCode().equals(status)) {
            throw new BusinessException(ResultCode.STATUS_OPERATE_FORBIDDEN,
                    "供应商处于【已发布】状态，暂不支持编辑，请先执行撤回操作");
        }
    }

    /**
     * 批量写入联系人
     */
    private void insertContacts(Long supplierId, List<SupplierContactSaveDTO> contacts) {
        for (SupplierContactSaveDTO c : contacts) {
            SupplierContact entity = BeanUtil.toBean(c, SupplierContact.class);
            entity.setSupplierId(supplierId);
            supplierContactMapper.insert(entity);
        }
    }

    /**
     * 整体替换指定供应商的联系人
     * <p>先校验全部入参归属，再删除旧数据，避免校验失败时已删掉一半。
     */
    private void replaceContacts(Long supplierId, List<SupplierContactUpdateDTO> contacts) {
        if (contacts.isEmpty()) {
            return;
        }
        for (SupplierContactUpdateDTO c : contacts) {
            SupplierContact db = supplierContactMapper.selectById(c.getContactId());
            if (db == null || !supplierId.equals(db.getSupplierId())) {
                throw new BusinessException(ResultCode.PARAM_VALUE_INVALID,
                        "联系人不存在或不属于该供应商：" + c.getContactId());
            }
        }

        LambdaQueryWrapper<SupplierContact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierContact::getSupplierId, supplierId);
        supplierContactMapper.delete(wrapper);

        for (SupplierContactUpdateDTO c : contacts) {
            SupplierContact entity = new SupplierContact();
            entity.setContactId(c.getContactId());
            entity.setSupplierId(supplierId);
            entity.setName(c.getName());
            entity.setPosition(c.getPosition());
            entity.setPhone(c.getPhone());
            entity.setEmail(c.getEmail());
            supplierContactMapper.insert(entity);
        }
    }

    /**
     * 把空白字符串规整为 null
     * <p>唯一索引允许多个 NULL，但第二个空串会撞键，故入库前统一规整。
     */
    private String normalizeNullable(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
