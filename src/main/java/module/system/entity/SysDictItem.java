package module.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字典项明细表 (sys_dict_item)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysDictItem {

    /** 主键ID（雪花算法，业务生成） */
    private Long dictItemId;

    /** 外键：sys_dict_type.dict_type_id 字典分类主键 */
    private Long dictTypeId;

    /** 前端下拉展示文字 */
    private String label;

    /** 数据库存储真实值 */
    private String value;

    /** 下拉选项排序 */
    private Integer sort;

    /** 状态：0-停用 1-启用 */
    private Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
