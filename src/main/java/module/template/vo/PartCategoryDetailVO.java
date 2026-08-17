package module.template.vo;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

@Data
public class PartCategoryDetailVO {
    
    /** 主键ID（雪花算法，业务生成） */
    @TableId
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
}
