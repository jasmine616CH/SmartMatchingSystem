package module.scheme.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 无人车顶层参数定义表 (vehicle_param_def)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleParamDef {

    /** 主键雪花ID */
    private Long id;

    /** 参数唯一编码，存入whole_car_req JSON的key */
    private String paramCode;

    /** 参数中文名称：整车额定载重、整车最高车速 */
    private String paramName;

    /** 单位：t、km/h、V、° */
    private String unit;

    /** 数据类型 number/enum/text/bool */
    private String dataType;

    /** 枚举参数关联全局字典编码，非枚举置空 */
    private String relDictCode;

    /** 页面展示排序号 */
    private Integer sort;

    /** 参数业务说明 */
    private String remark;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
