package module.supplier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AdminSupplierDTO {

    /**
     * 供应商雪花id
     */
    private Long supplierId;

    /**
     * 供应商联系人雪花id
     */
    private Long contactId;

    /**
     * 供应商企业全称
     */
    private String supplierName;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

    /**
     * 企业注册/办公地址
     */
    private String address;

    /**
     * 供应商备注
     */
    private String remark;

    /**
     * 供应商可供应配件品类范围
     */
    private String supplierScope;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 联系人岗位职位
     */
    private String position;

    /**
     * 联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$" , message = "手机号格式不正确，请输入正确的格式")
    private String phone;

    /**
     * 联系邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "邮箱格式错误，请输入有效邮箱")
    private String email;

}
