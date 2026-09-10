package module.part.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class PartParamValueVO {
    
    /** 主键ID（雪花算法，业务生成） */
    private Long paramValId;

    /** 外键：part_info.part_id 所属配件 */
    private Long partId;

    /** 外键：part_template.template_id 所属配件模板 */
    private Long templateId;

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
    private LocalDateTime recordDate;
}
