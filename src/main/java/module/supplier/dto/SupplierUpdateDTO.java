package module.supplier.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改供应商入参
 * <p>
 * 全量覆盖式更新（联系人列表整体替换）。status / auditUserId 由服务端派生，
 * 不接受前端传入。
 */
@Data
public class SupplierUpdateDTO {

    /** 供应商主键ID */
    @NotNull(message = "supplierId 不能为空")
    private Long supplierId;

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

    /**
     * 联系人列表，整体替换该供应商现有联系人。
     * <p>不传或为空表示不修改联系人（只改主体信息）。
     */
    @Valid
    private List<SupplierContactUpdateDTO> contacts;
}
