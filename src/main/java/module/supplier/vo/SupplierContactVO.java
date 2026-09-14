package module.supplier.vo;

import lombok.Data;

/**
 * 供应商联系人
 */
@Data
public class SupplierContactVO {

    /** 联系人主键ID */
    private Long contactId;

    /** 所属供应商主键ID */
    private Long supplierId;

    /** 联系人姓名 */
    private String name;

    /** 联系人岗位职位 */
    private String position;

    /** 联系电话 */
    private String phone;

    /** 联系邮箱 */
    private String email;
}
