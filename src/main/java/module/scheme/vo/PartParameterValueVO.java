package module.scheme.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartParameterValueVO {

    /** 配件型号 */
    private String model;

    /** 全局唯一物料编码 */
    private String partCode;

    /** 配件品牌 */
    private String brand;

    /** 展示核心参数 */
    private List<ParamVo> coreParam;

    /** 展示价格核心参数 */
    private PriceVO priceVo;

}
