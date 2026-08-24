package module.supplier.vo;


import cn.hutool.core.date.DateTime;
import lombok.Data;

@Data
public class QuerySupplierVo {

    /**
     * 统一社会信用编码
     */
    private String creditCode;

    /**
     * 供应商名字
     */
    private String supplierName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String phone;

    /**
     * 供应商地址
     */
    private String address;

    /**
     * 供应商状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private DateTime creatTime;

}
