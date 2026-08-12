package module.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统角色表 (sys_role)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRole {

    /** 主键ID（雪花算法，业务生成） */
    private Long roleId;

    /** 角色中文名称 */
    private String roleName;

    /** 角色唯一编码，权限标识 */
    private String roleCode;

    /** 角色职责说明 */
    private String remark;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
