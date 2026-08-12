package module.scheme.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 选型方案主表 (selection_scheme)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectionScheme {

    /** 主键ID（雪花算法，业务生成） */
    private Long schemeId;

    /** 外键：user.user_id 创建方案的工程师ID */
    private Long userId;

    /** 方案自定义名称 */
    private String schemeName;

    /** 整车顶层筛选条件JSON，存储vehicle_param_def定义的参数 */
    private String wholeCarReq;

    /** 方案整体备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
