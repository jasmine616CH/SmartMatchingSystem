package module.system.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 字典项
 */
@Data
public class DictItemVO {

    /** 字典项主键ID */
    private Long dictItemId;

    /** 所属字典类型ID */
    private Long dictTypeId;

    /** 数据库存储真实值 */
    private String value;

    /** 前端下拉展示文字 */
    private String label;

    /** 排序号 */
    private Integer sort;

    /** 状态：0-停用 1-启用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
