package module.scheme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 复制方案入参
 * <p>用于历史方案复用：把原方案的配件明细整体复制到新方案，原方案不受影响。
 */
@Data
public class SchemeCopyDTO {

    /** 新方案名称，全局唯一 */
    @NotBlank(message = "方案名称不能为空")
    @Size(max = 100, message = "方案名称最多100字")
    private String schemeName;

    /** 新方案备注，不传则沿用原方案备注 */
    @Size(max = 500, message = "方案备注最多500字")
    private String remark;
}
