package module.supplier.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 供应商联系人表 (supplier_contact)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierContact {

    /** 主键ID（雪花算法，业务生成） */
    private Long contactId;

    /** 外键：supplier.supplier_id 所属供应商 */
    private Long supplierId;

    /** 联系人姓名 */
    private String name;

    /** 联系人岗位职位 */
    private String position;

    /** 联系电话 */
    private String phone;

    /** 联系邮箱 */
    private String email;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
