package module.compat.vo;

import lombok.Data;

/**
 * 参数编码候选
 * <p>
 * 供冲突规则的表达式编辑器做自动补全：选定子系统后，
 * 列出其下所有已发布模板里实际存在的 param_code，用户从里面挑，不用手敲。
 */
@Data
public class ParamCodeOptionVO {

    /** 参数编码，表达式里通过 a.该编码 / b.该编码 访问 */
    private String paramCode;

    /** 参数中文名 */
    private String paramCn;

    /** 参数英文名 */
    private String paramEn;

    /** 工程单位 */
    private String unit;

    /** 数据类型：number/enum/bool/text/date */
    private String dataType;

    /** 来源模板名称，便于区分同名参数 */
    private String templateName;

    /** 来源三级分类名称 */
    private String catName;
}
