package module.supplier.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商资质档案表 (supplier_qualification)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierQualification {

    /** 主键ID（雪花算法，业务生成） */
    private Long qfId;

    /** 外键：supplier.supplier_id 所属供应商 */
    private Long supplierId;

    /** 资质证书名称 */
    private String qfName;

    /** 证书编号 */
    private String certNo;

    /** 证书生效日期 */
    private LocalDate effectDate;

    /** 证书失效日期 */
    private LocalDate expireDate;

    /** 证书扫描件文件地址 */
    private String fileUrl;

    /** 状态：0-过期 1-有效 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
