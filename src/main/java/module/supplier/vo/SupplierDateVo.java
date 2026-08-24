package module.supplier.vo;

import lombok.Data;

@Data
public class SupplierDateVo {

    /**
     * 供应商联系人雪花id
     */
    private Long contactId;

    /**
     * 供应商企业全称
     */
    private String supplierName;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

    /**
     * 企业注册/办公地址
     */
    private String address;

    /**
     * 供应商备注
     */
    private String remark;

    /**
     * 供应商可供应配件品类范围
     */
    private String supplierScope;

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 联系人岗位职位
     */
    private String position;

    /**
     * 联系电话
     */

    /**
     * 联系邮箱
     */
    private String email;

}
