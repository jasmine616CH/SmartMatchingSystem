package module.template.vo;

import java.util.List;


import lombok.Data;

@Data
public class PartCategoryTreeVO {
    
    /** 主键ID（雪花算法，业务生成） */
    private Long catId;

    /** 自关联外键，0为一级八大系统 */
    private Long parentCatId;

    /** 分类名称 */
    private String catName;

    /** 子分类 */
    private List<PartCategoryTreeVO> children;
}
