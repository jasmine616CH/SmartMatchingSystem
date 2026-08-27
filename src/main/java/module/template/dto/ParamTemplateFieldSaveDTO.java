package module.template.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParamTemplateFieldSaveDTO {

    // 外键：param_template.template_id
    @NotNull(message = "templateId 不能为空")
    private Long templateId;

    // 参数唯一编码
    @NotBlank(message = "paramCode 不能为空")
    private String paramCode;

    // 参数中文名称
    @NotBlank(message = "paramCn 不能为空")
    private String paramCn;

    // 参数英文名称
    @NotBlank(message = "paramEn 不能为空")
    private String paramEn;

    // 数据类型
    @NotBlank(message = "dataType 不能为空")
    private String dataType;

    // 枚举参数关联全局字典编码，data_type=enum时生效
    @NotBlank(message = "relDictCode 不能为空")
    private String relDictCode;

    // 工程单位
    @NotBlank(message = "unit 不能为空")
    private String unit;

     /**
     * JSR385 Indriya标准单位编码，如 KILOWATT、MILLIMETRE
     * number类型必填，用于后端单位换算、表达式计算
     */
    private String stdUnitCode;

    // 数值小数精度
    @NotNull(message = "precision 不能为空")
    private Integer precision;

    // 参数容差范围，如±3%
    @NotBlank(message = "tolerance 不能为空")
    private String tolerance;

    // 必填类型：0-非必填 1-全局必填 2-条件必填
    @NotNull(message = "requiredType 不能为空")
    private Integer requiredType;

     /**
     * Aviator条件必填表达式
     * required_type=2 条件必填时生效，示例：#CHASSIS_STRUCT_TYPE=="truss"
     */
    private String requiredExpression;

    // 前端筛选支持运算符：>=、<=、区间、多选
    @NotBlank(message = "filterOperator 不能为空")
    private String filterOperator;

    // 参数适用环境限制说明
    @NotBlank(message = "envLimit 不能为空")
    private String envLimit;

    // 页面展示排序
    @NotNull(message = "sort 不能为空")
    private Integer sort;
}
