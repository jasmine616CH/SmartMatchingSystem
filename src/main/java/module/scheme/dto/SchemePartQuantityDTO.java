package module.scheme.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 批量修改明细数量入参
 * <p>
 * 单条修改就是长度为 1 的数组，前端批量编辑一次提交。
 */
@Data
public class SchemePartQuantityDTO {

    /** 待修改的明细，至少一条 */
    @Valid
    @NotEmpty(message = "至少提交一条明细")
    private List<Item> items;

    /**
     * 单条明细数量
     */
    @Data
    public static class Item {

        /** 明细主键ID */
        @NotNull(message = "schemePartId 不能为空")
        private Long schemePartId;

        /** 选用数量 */
        @NotNull(message = "quantity 不能为空")
        @Min(value = 1, message = "数量必须大于等于1")
        private Integer quantity;
    }
}
