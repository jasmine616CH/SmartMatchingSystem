package module.scheme.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

/**
 * 方案详情
 */
@Data
public class SchemeDetailVO {

    /** 方案主键ID */
    private Long schemeId;

    /** 方案名称 */
    private String schemeName;

    /** 整车顶层筛选条件，key 来自 vehicle_param_def.param_code */
    private Map<String, Object> wholeCarReq;

    /** 方案整体备注 */
    private String remark;

    /** 创建人用户ID */
    private Long userId;

    /** 创建人姓名 */
    private String userName;

    /** 配件数量 */
    private Integer partCount;

    /** 方案总价 */
    private BigDecimal totalPrice;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
