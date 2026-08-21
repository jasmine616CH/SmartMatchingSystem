package module.supplier.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class QuerySupplierDTO {

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
     * 供应商状态
     */
    private Integer status;

    //分页查询

    /**
     * 每页最大条数
     */
    @Min(value = 1 , message = "每页最小条数为1")
    @Max(value = 100 , message = "每页最大数为100")
    private Integer pageSize;

    /**
     * 页码数
     */
    @Min(value = 1 , message = "页码最小数为1")
    private Integer pageNum;

}
