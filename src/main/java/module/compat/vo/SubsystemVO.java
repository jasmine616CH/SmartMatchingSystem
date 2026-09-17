package module.compat.vo;

import lombok.Data;

/**
 * 二级子系统选项
 * <p>
 * 冲突规则只能挂在二级子系统上，规则管理页的两侧下拉直接用这个列表，
 * 不必去拉整棵分类树再自己过滤层级。
 */
@Data
public class SubsystemVO {

    /** 子系统主键ID */
    private Long catId;

    /** 子系统名称，如「驱动电机」 */
    private String catName;

    /** 子系统编码 */
    private String catCode;

    /** 所属一级系统ID */
    private Long parentCatId;

    /** 所属一级系统名称，如「驱动系统」 */
    private String parentCatName;
}
