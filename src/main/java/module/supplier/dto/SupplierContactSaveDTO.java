package module.supplier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 供应商联系人入参（新增场景）
 */
@Data
public class SupplierContactSaveDTO {

    /** 联系人姓名 */
    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 30, message = "联系人姓名最多30字")
    private String name;

    /** 联系人岗位职位 */
    @Size(max = 50, message = "联系人岗位职位最多50字")
    private String position;

    /** 联系电话 */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确，请输入正确的格式")
    private String phone;

    /** 联系邮箱，选填 */
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "邮箱格式错误，请输入有效邮箱")
    private String email;
}
