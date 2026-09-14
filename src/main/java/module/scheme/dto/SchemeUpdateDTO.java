package module.scheme.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改方案基本信息入参
 * <p>
 * 不涉及配件明细，明细的增删改走各自的接口。
 */
@Data
public class SchemeUpdateDTO {

    /** 方案主键ID */
    @NotNull(message = "schemeId 不能为空")
    private Long schemeId;

    /** 方案名称，全局唯一 */
    @NotBlank(message = "方案名称不能为空")
    @Size(max = 100, message = "方案名称最多100字")
    private String schemeName;

    /**
     * 整车顶层筛选条件
     * <p>传 null 表示不修改；要清空请传空对象 {}。
     */
    private Map<String, Object> wholeCarReq;

    /** 方案整体备注 */
    @Size(max = 500, message = "方案备注最多500字")
    private String remark;
}
