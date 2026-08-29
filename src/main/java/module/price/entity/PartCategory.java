package module.price.entity;


import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配件三级分类表（树形结构） (part_category)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartCategory {

    /** 主键id（雪花算法，业务生成） */
    private Long catId;

    /** 自关联外键，0为一级八大系统 */
    private Long parentCatId;

    /** 分类名称 */
    private String catName;

    /** 分类全局唯一编码 */
    private String catCode;

    /** 层级：1-系统 2-子系统 3-配件类别 */
    private int level;

    /** 树形展示排序 */
    private int sort;

    /** 分类业务说明 */
    private String remark;

    /** 记录创建时间 */
    private DateTime createTime;

    /** 记录更新时间 */
    private DateTime updateTime;

}
