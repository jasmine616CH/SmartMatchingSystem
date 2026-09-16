package module.scheme.vo;

import java.util.List;

import lombok.Data;

/**
 * 并排比较矩阵中的一行：一个参数
 */
@Data
public class CompareRowVO {

    /** 模板参数字段ID */
    private Long fieldId;

    /** 参数编码 */
    private String paramCode;

    /** 参数中文名 */
    private String paramCn;

    /** 工程单位 */
    private String unit;

    /** 数据类型：number/enum/bool/text/date */
    private String dataType;

    /** 是否必填：0-非必填 1-全局必填 2-条件必填 */
    private Integer requiredType;

    /**
     * 各配件在该参数上的判定结果
     * <p>顺序与 {@link SchemeCompareVO#getParts()} 一一对应，前端可直接按下标对齐渲染。
     */
    private List<CompareCellVO> cells;
}
