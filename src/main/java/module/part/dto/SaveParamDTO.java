package module.part.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 单条参数保存入参（方案A：逐个录入）
 * <p>
 * numValue 与 textValue 二选一，区间参数填 numMin/numMax。
 * 四个值全部为空视为"清空该字段"。
 */
@Data
public class SaveParamDTO {

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
}
