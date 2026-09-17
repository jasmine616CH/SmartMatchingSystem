package module.supplier.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增供应商入参
 * <p>
 * 供应商主体与其联系人一次提交。status / auditUserId 由服务端派生，
 * 新建时状态固定为「草稿」，不接受前端传入。
 */
@Data
public class SupplierSaveDTO {

    /** 供应商企业全称 */
    @NotBlank(message = "供应商企业全称不能为空")
    @Size(max = 100, message = "供应商企业全称最多100字")
    private String supplierName;

    /** 统一社会信用代码，全局唯一 */
    @Size(max = 50, message = "统一社会信用代码最多50字")
    private String creditCode;

    /** 企业注册/办公地址 */
    @Size(max = 500, message = "企业注册/办公地址最多500字")
    private String address;

    /** 可供应配件品类范围 */
    private String supplyScope;

    /** 供应商备注 */
    @Size(max = 500, message = "供应商备注最多500字")
    private String remark;

    /** 联系人列表，至少一条 */
    @Valid
    @NotEmpty(message = "至少填写一个联系人")
    private List<SupplierContactSaveDTO> contacts;
}
