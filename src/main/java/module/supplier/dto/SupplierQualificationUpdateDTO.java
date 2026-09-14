package module.supplier.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改供应商资质入参
 * <p>
 * supplierId 只用于校验，不允许变更资质所属供应商。
 */
@Data
public class SupplierQualificationUpdateDTO {

    /** 资质主键ID */
    @NotNull(message = "qfId 不能为空")
    private Long qfId;

    /** 外键：supplier.supplier_id 所属供应商，必须与原值一致 */
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

    /** 证书失效日期 */
    @NotNull(message = "expireDate 不能为空")
    private LocalDate expireDate;

    /** 证书扫描件文件地址 */
    @Size(max = 500, message = "证书扫描件文件地址最多500字")
    private String fileUrl;
}
