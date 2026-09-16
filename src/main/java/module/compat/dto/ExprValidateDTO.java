package module.compat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 表达式语法校验入参
 * <p>供表达式编辑器边输入边校验，不必先保存规则。
 */
@Data
public class ExprValidateDTO {

    /** 待校验的 Aviator 表达式 */
    @NotBlank(message = "表达式不能为空")
    @Size(max = 1000, message = "表达式最多1000字")
    private String checkExpr;
}
