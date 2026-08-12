package module.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字典类型表 (sys_dict_type)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysDictType {

    /** 主键ID（雪花算法，业务生成） */
    private Long dictTypeId;

    /** 字典分类中文名称 */
    private String dictName;

    /** 字典唯一编码，关联参数模板 */
    private String dictCode;

    /** 状态：0-停用 1-启用 */
    private Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
