package module.scheme.vo;

import lombok.Data;

@Data
public class ParamVo {

    private Long fieldId;

    /** 中文名 */
    private String paramCn;

    /** 数据类型 */
    private String dataType;

    /** 单位 */
    private String unit;

    /** 展示值 */
    private String displayValue;

    /** 区间最小值 */
    private String minValue;

    /** 区间最大值 */
    private String maxValue;

}
