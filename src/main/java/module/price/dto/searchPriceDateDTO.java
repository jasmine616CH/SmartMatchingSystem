package module.price.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class searchPriceDateDTO {

    /**
     * 价格类型
     */
    private String priceType;

    /**
     * 配件
     */
    private String partName;

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
