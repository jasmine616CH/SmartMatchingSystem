package module.review.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.review.dto.AuditTaskQueryDTO;
import module.review.entity.AuditRecord;
import module.review.vo.AuditRecordVO;

/**
 * 通用审批记录表 (audit_record)
 * <p>
 * 单表增删改继承 BaseMapper；需要联 sys_user 取提交人/审批人姓名以及分页动态条件，
 * 写在 resources/mapper/review/AuditRecordMapper.xml。
 */
@Mapper
public interface AuditRecordMapper extends BaseMapper<AuditRecord> {

    /**
     * 分页查询审批任务列表（联表带出提交人、审批人姓名）
     * <p>
     * page 必须是首个入参，分页插件据此改写 SQL 并回填 total。
     *
     * @param page 分页对象
     * @param dto  查询条件，全部可选
     * @return 分页结果
     */
    Page<AuditRecordVO> selectAuditPage(Page<AuditRecordVO> page,
            @Param("dto") AuditTaskQueryDTO dto);

    /**
     * 查询某业务对象的完整审批历史（审批时间倒序）
     *
     * @param bizType 业务类型编码
     * @param bizId   业务对象主键
     * @return 审批记录列表
     */
    List<AuditRecordVO> selectHistory(@Param("bizType") String bizType,
            @Param("bizId") Long bizId);

    /**
     * 查询某业务对象最近一条审批记录（不限状态）
     * <p>撤回时需要取最新的一条判断它是否为「已通过」。
     *
     * @param bizType 业务类型编码
     * @param bizId   业务对象主键
     * @return 最近一条记录，不存在返回 null
     */
    @Select("SELECT * FROM audit_record WHERE biz_type = #{bizType} AND biz_id = #{bizId} "
            + "ORDER BY audit_id DESC LIMIT 1")
    AuditRecord selectLatestByBiz(@Param("bizType") String bizType,
            @Param("bizId") Long bizId);

    /**
     * 统计某业务对象当前待审核的任务条数
     * <p>删除、编辑前的引用检查：存在待办任务时不允许操作。
     *
     * @param bizType 业务类型编码
     * @param bizId   业务对象主键
     * @return 待审核条数
     */
    @Select("SELECT COUNT(1) FROM audit_record WHERE biz_type = #{bizType} "
            + "AND biz_id = #{bizId} AND status = 0")
    long countPending(@Param("bizType") String bizType, @Param("bizId") Long bizId);
}
