package module.supplier.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 供应商联系人入参（修改场景）
 * <p>
 * 与新增的差别：带主键，且字段允许留空表示不修改该字段（部分更新）。
 */
@Data
public class SupplierContactUpdateDTO {

    /** 联系人主键ID */
    @NotNull(message = "contactId 不能为空")
    private Long contactId;

    /** 联系人姓名 */
    @Size(max = 30, message = "联系人姓名最多30字")
    private String name;

    /** 联系人岗位职位 */
    @Size(max = 50, message = "联系人岗位职位最多50字")
    private String position;

    /** 联系电话 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确，请输入正确的格式")
    private String phone;

    /** 联系邮箱 */
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "邮箱格式错误，请输入有效邮箱")
    private String email;
}
