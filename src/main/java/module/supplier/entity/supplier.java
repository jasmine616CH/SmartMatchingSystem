package module.supplier.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 供应商主体表 (supplier)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class supplier {

    /** 主键ID（雪花算法，业务生成） */
    private Long supplierId;

    /** 供应商企业全称 */
    private String supplierName;

    /** 统一社会信用代码 */
    private String creditCode;

    /** 企业注册/办公地址 */
    private String address;

    /** 可供应配件品类范围 */
    private String supplyScope;

    /** 状态：0-停用 1-启用 */
    private Integer status;

    /** 供应商备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
