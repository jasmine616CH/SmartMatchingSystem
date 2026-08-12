package module.part.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 配件参数值表 (part_param_value)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartParamValue {

    /** 主键ID（雪花算法，业务生成） */
    private Long paramValId;

    /** 外键：part_info.part_id 所属配件 */
    private Long partId;

    /** 绑定模板参数字段ID，对应参数定义 */
    private Long fieldId;

    /** number类型参数数值 */
    private BigDecimal numValue;

    /** text/enum/bool类型参数值 */
    private String textValue;

    /** 区间参数最小值 */
    private BigDecimal numMin;

    /** 区间参数最大值 */
    private BigDecimal numMax;

    /** 参数录入版本号 */
    private String recordVersion;

    /** 参数录入日期 */
    private LocalDate recordDate;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
