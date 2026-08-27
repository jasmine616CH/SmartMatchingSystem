package module.template.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 模板参数字段明细表 (param_template_field)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamTemplateField {

    /** 主键ID（雪花算法，业务生成） */
    @TableId
    private Long fieldId;

    /** 外键：param_template.template_id 所属参数模板 */
    private Long templateId;

    /** 参数唯一编码，如MOTOR_RATED_POWER */
    private String paramCode;

    /** 参数中文名称 */
    private String paramCn;

    /** 参数英文名称 */
    private String paramEn;

    /** 数据类型：number/enum/bool/text/date */
    private String dataType;

    /** 枚举参数关联全局字典编码，data_type=enum时生效 */
    private String relDictCode;

    /** 工程单位，如kW、N·m、V、mm */
    private String unit;

    /**
     * JSR385 Indriya标准单位编码，如 KILOWATT、MILLIMETRE
     * number类型必填，用于后端单位换算、表达式计算
     */
    private String stdUnitCode;

    /** 数值小数精度 */
    private Integer precision;

    /** 参数容差范围，如±3% */
    private String tolerance;

    /** 必填类型：0-非必填 1-全局必填 2-条件必填 */
    private Integer requiredType;

    /**
     * Aviator条件必填表达式
     * required_type=2 条件必填时生效，示例：#CHASSIS_STRUCT_TYPE=="truss"
     */
    private String requiredExpression;

    /** 前端筛选支持运算符：>=、<=、区间、多选 */
    private String filterOperator;

    // /** 参数数据来源：规格书/实测/计算/第三方检测 */
    // private String dataSource;

    /** 参数适用环境限制说明 */
    private String envLimit;

    /** 页面展示排序 */
    private Integer sort;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
