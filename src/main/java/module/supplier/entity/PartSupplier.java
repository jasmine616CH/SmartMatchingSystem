package module.supplier.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配件-供应商关联表 (part_supplier)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartSupplier {

    /** 主键ID（雪花算法，业务生成） */
    private Long psId;

    /** 外键：part_info.part_id 配件主键 */
    private Long partId;

    /** 外键：supplier.supplier_id 供应商主键 */
    private Long supplierId;

    /** 供货类型：main-主供 spare-备供 wait-待认证 */
    private String supplyType;

    /** 认证状态：0-未认证 1-认证通过，未认证厂商不展示采购BOM */
    private Integer authStatus;

    /** 供货备注说明 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
