package module.scheme.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 方案候选配件明细表 (scheme_part)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchemePart {

    /** 主键ID（雪花算法，业务生成） */
    private Long schemePartId;

    /** 外键：selection_scheme.scheme_id 所属方案 */
    private Long schemeId;

    /** 外键：part_info.part_id 选配配件 */
    private Long partId;

    /** 配件匹配整车需求得分，方案A智能匹配生成 */
    private BigDecimal matchScore;

    /** 是否纳入最终BOM：0-候选 1-正式BOM配件 */
    private Integer isSelectBom;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
