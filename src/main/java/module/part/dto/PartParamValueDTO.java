package module.part.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PartParamValueDTO {

    // 外键：part_info.part_id 所属配件
    @NotNull(message = "partId 不能为空")
    private Long partId;

    // 绑定模板参数字段ID，对应参数定义
    @NotNull(message = "fieldId 不能为空")
    private Long fieldId;

    //  number类型参数数值
    @NotNull(message = "numValue 不能为空")
    private BigDecimal numValue;

    // text/enum/bool类型参数值
    @NotBlank(message = "textValue 不能为空")
    private String textValue;

    // 区间参数最小值
    @NotNull(message = "numMin 不能为空")
    private BigDecimal numMin;

    // 区间参数最大值
    @NotNull(message = "numMax 不能为空")
    private BigDecimal numMax;

    // 参数录入版本号
    @NotBlank(message = "recordVersion 不能为空")
    private String recordVersion;

    // 参数录入日期
    @NotNull(message = "recordDate 不能为空")
    private LocalDateTime recordDate;
}
