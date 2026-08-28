package module.template.vo;

import java.util.List;

import lombok.Data;
import module.template.entity.ParamFieldCheckRule;

@Data
public class ParamTemplateFieldVO {
    // 主键ID（雪花算法，业务生成）
    private Long fieldId;

    // 外键：param_template.template_id
    private Long templateId;

    // 参数唯一编码
    private String paramCode;

    // 参数中文名称
    private String paramCn;

    // 参数英文名称
    private String paramEn;

    // 数据类型
    private String dataType;

    // 枚举参数关联全局字典编码，data_type=enum时生效
    private String relDictCode;

    // 工程单位
    private String unit;

    // JSR385 Indriya标准单位编码，如 KILOWATT、MILLIMETRE
    private String stdUnitCode;

    // 数值小数精度
    private Integer precision;

    // 参数容差范围，如±3%
    private String tolerance;

    // 必填类型：0-非必填 1-全局必填 2-条件必填
    private Integer requiredType;

    // Aviator条件必填表达式
    private List<ParamFieldCheckRuleVO> checkRuleList;

    // 前端筛选支持运算符：>=、<=、区间、多选
    private String filterOperator;

    // 参数数据来源：规格书/实测/计算/第三方检测
    private String dataSource;

    // 参数适用环境限制说明
    private String envLimit;

    // 页面展示排序
    private Integer sort;

}
