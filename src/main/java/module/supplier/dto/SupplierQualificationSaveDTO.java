package module.supplier.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增供应商资质入参
 * <p>
 * status / auditUserId 由服务端派生，新建时状态固定为「草稿」。
 */
@Data
public class SupplierQualificationSaveDTO {

    /** 外键：supplier.supplier_id 所属供应商 */
    @NotNull(message = "supplierId 不能为空")
    private Long supplierId;

    /** 资质证书名称 */
    @NotBlank(message = "资质证书名称不能为空")
    @Size(max = 100, message = "资质证书名称最多100字")
    private String qfName;

    /** 证书编号，同一供应商下唯一 */
    @Size(max = 100, message = "证书编号最多100字")
    private String certNo;

    /** 证书生效日期 */
    @NotNull(message = "effectDate 不能为空")
    private LocalDate effectDate;

    /**
     * 证书失效日期
     * <p>允许填写已过去的日期（补录历史证书是合法的，只是会立即展示为「过期」），
     * 但已过期的资质不允许提交审核。
     */
    @NotNull(message = "expireDate 不能为空")
    private LocalDate expireDate;

    /** 证书扫描件文件地址 */
    @Size(max = 500, message = "证书扫描件文件地址最多500字")
    private String fileUrl;
}
