package module.scheme.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 候选件并排比较入参
 */
@Data
public class SchemeCompareQueryDTO {

    /** 三级配件分类ID，参与比较的配件都属于该分类 */
    @NotNull(message = "catId 不能为空")
    private Long catId;

    /**
     * 要比对的配件ID列表
     * <p>不传则取该分类下全部已发布配件（最多 {@code MAX_CANDIDATES} 个）。
     */
    private List<Long> partIds;
}
