package module.part.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配件三级分类表（树形结构） (part_category)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartCategory {

    /** 主键ID（雪花算法，业务生成） */
    private Long catId;

    /** 自关联外键，0为一级八大系统 */
    private Long parentCatId;

    /** 分类名称 */
    private String catName;

    /** 分类全局唯一编码 */
    private String catCode;

    /** 层级：1-系统 2-子系统 3-配件类别 */
    private Integer level;

    /** 树形展示排序 */
    private Integer sort;

    /** 分类业务说明 */
    private String remark;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
