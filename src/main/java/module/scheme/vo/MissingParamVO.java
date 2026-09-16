package module.scheme.vo;

import lombok.Data;

/**
 * 缺失参数项
 * <p>
 * 「缺失」是四种判定态之一（满足 / 临界 / 不满足 / 缺失），
 * 指配件没有录入该参数的值。此处只收录必填（含条件必填成立）的参数，
 * 非必填参数缺失属正常情况，不进此列表。
 */
@Data
public class MissingParamVO {

    /** 模板参数字段ID */
    private Long fieldId;

    /** 参数编码 */
    private String paramCode;

    /** 参数中文名 */
    private String paramCn;

    /** 工程单位 */
    private String unit;

    /** 必填类型：1-全局必填 2-条件必填 */
    private Integer requiredType;

    /** 必填类型中文描述 */
    private String requiredTypeName;

    /** 该参数下启用的规则条数，便于前端判断缺失的严重程度 */
    private Integer ruleCount;
}
