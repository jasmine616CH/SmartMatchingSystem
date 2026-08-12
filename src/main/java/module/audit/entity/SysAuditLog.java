package module.audit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审计日志表 (sys_audit_log)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysAuditLog {

    /** 主键ID（雪花算法，业务生成） */
    private Long logId;

    /** 外键：user.user_id 操作人ID */
    private Long operateUserId;

    /** 操作人姓名 */
    private String realName;

    /** 操作模块：配件管理/供应商/价格/参数模板 */
    private String operateModule;

    /** 操作类型：新增/编辑/停用/启用/审核/删除 */
    private String operateType;

    /** 被操作的数据表名称 */
    private String targetTable;

    /** 被操作数据的主键ID */
    private Long targetId;

    /** 变更前完整JSON数据 */
    private String oldValue;

    /** 变更后完整JSON数据 */
    private String newValue;

    /** 操作客户端IP地址 */
    private String operateIp;

    /** 操作发生时间 */
    private LocalDateTime operateTime;

    /** 日志记录创建时间 */
    private LocalDateTime createTime;

    /** 日志更新时间 */
    private LocalDateTime updateTime;
}
