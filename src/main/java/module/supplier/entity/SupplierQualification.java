package module.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableId;

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
    @TableId
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

    /**
     * 状态：0-草稿 1-待审核 2-有效
     * <p>3-过期 由 expire_date 读时派生，不落库，见 SupplierQualificationStatus#resolveDisplayStatus
     */
    private Integer status;

    /** 外键：sys_user.user_id 审批人用户ID，草稿状态为空 */
    private Long auditUserId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
