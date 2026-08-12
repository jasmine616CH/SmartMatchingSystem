package module.scheme.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配件兼容冲突日志表 (scheme_conflict_log)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchemeConflictLog {

    /** 主键ID（雪花算法，业务生成） */
    private Long conflictId;

    /** 外键：selection_scheme.scheme_id 所属方案 */
    private Long schemeId;

    /** 冲突配件A主键 */
    private Long partAId;

    /** 冲突配件B主键 */
    private Long partBId;

    /** 发生冲突的参数名称 */
    private String conflictParam;

    /** 冲突详细文字描述 */
    private String conflictDesc;

    /** 冲突整改解决建议，可选填 */
    private String solveSuggest;

    /** 冲突检测生成时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
