package module.system.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 字典类型
 */
@Data
public class DictTypeVO {

    /** 字典类型主键ID */
    private Long dictTypeId;

    /** 字典大类编码 */
    private String dictCategory;

    /** 字典唯一编码 */
    private String dictCode;

    /** 字典名称 */
    private String dictName;

    /** 状态：0-停用 1-启用 */
    private Integer status;

    /** 该字典下的字典项数量 */
    private Integer itemCount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
