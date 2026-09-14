package module.supplier.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 供应商资质
 */
@Data
public class SupplierQualificationVO {

    /** 资质主键ID */
    private Long qfId;

    /** 所属供应商主键ID */
    private Long supplierId;

    /** 所属供应商企业全称 */
    private String supplierName;

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

    /**
     * 展示用状态：0-草稿 1-待审核 2-有效 3-过期
     * <p>3-过期 为读时派生，数据库里存的是 2-有效。
     */
    private Integer status;

    /** 状态中文描述 */
    private String statusName;

    /** 数据库实际存储的状态（不含过期投影） */
    private Integer storedStatus;

    /** 审批人用户ID，草稿状态为空 */
    private Long auditUserId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
