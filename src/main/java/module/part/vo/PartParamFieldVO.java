package module.part.vo;

import lombok.Data;

@Data 
public class PartParamFieldVO {

     // === 来自模板字段定义 ===
    private Long    fieldId;
    private String  paramCode;        // 表达式里的变量名
    private String  paramCn;          // 中文名，给用户看
    private String  paramUnit;
    private String  dataType;
    private Integer requiredType;     // 0非必填 1全局必填 2条件必填
    private String  requiredExpression;
    private Integer sort;

    // === 运行时算出来的（buildVO 负责填） ===
    private Boolean showFlag;         // 要不要在左侧显示
    private Boolean requiredFlag;     // 当前是不是必填
    private Boolean hasValue;         // 有没有录过值
    private Object  paramValue;       // 录过的值（回显）
    private String  statusTag;        // FILLED / REQUIRED_UNFILLED / OPTIONAL
}
