package module.template.vo;

import lombok.Data;

@Data
public class CurrentTemplateFieldListVO {

    /** 主键ID（雪花算法，业务生成） */
    private Long fieldId;

    /** 参数唯一编码，如MOTOR_RATED_POWER */
    private String paramCode;

    /** 参数中文名称 */
    private String paramCn;

    /** 参数英文名称 */
    private String paramEn;

    /** 数据类型：number/enum/bool/text/date */
    private String dataType;
}
