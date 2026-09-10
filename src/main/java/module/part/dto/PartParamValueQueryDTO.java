package module.part.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 * 配件参数值 - 查询出参
 * 用于组装 Map<paramCode, value> 喂给 Aviator
 */
@Data
public class PartParamValueQueryDTO {

    /** 参数编码，作为 paramMap 的 key */
    private String paramCode;

    /** 数据类型：number / enum / bool / text / date */
    private String dataType;

    /** number 类型值 */
    private BigDecimal numValue;

    /** enum / text / bool 类型值 */
    private String textValue;

    /** 区间参数最小值 */
    private BigDecimal numMin;

    /** 区间参数最大值 */
    private BigDecimal numMax;
}