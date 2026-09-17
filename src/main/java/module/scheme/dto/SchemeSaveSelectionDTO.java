package module.scheme.dto;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 保存选配结果为方案入参
 * <p>
 * 同一三级分类下只能选择一款配件，由服务端校验。
 */
@Data
public class SchemeSaveSelectionDTO {

    /** 方案名称，全局唯一 */
    @NotBlank(message = "方案名称不能为空")
    @Size(max = 100, message = "方案名称最多100字")
    private String schemeName;

    /**
     * 整车顶层筛选条件，key 来自 vehicle_param_def.param_code
     * <p>库中该列 NOT NULL，不传时服务端存 {}。
     */
    private Map<String, Object> wholeCarReq;

    /** 方案整体备注 */
    @Size(max = 500, message = "方案备注最多500字")
    private String remark;

    /** 选配明细，至少一条 */
    @Valid
    @NotEmpty(message = "至少选择一个配件")
    private List<Item> items;

    /**
     * 单条选配明细
     */
    @Data
    public static class Item {

        /** 三级配件分类ID，用于校验「同分类只能一款」 */
        @NotNull(message = "catId 不能为空")
        private Long catId;

        /** 配件主键ID */
        @NotNull(message = "partId 不能为空")
        private Long partId;

        /** 选用数量 */
        @NotNull(message = "quantity 不能为空")
        @Min(value = 1, message = "数量必须大于等于1")
        private Integer quantity;
    }
}
