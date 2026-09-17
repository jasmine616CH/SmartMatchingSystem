package module.scheme.vo;

import java.util.List;

import lombok.Data;

/**
 * 候选件并排比较结果
 * <p>
 * 形状是「参数 × 配件」的矩阵：rows 是参数行，parts 是配件列，
 * 每行的 cells 与 parts 顺序一一对应，前端可直接渲染成对比表。
 */
@Data
public class SchemeCompareVO {

    /** 三级配件分类ID */
    private Long catId;

    /** 分类名称 */
    private String catName;

    /** 参与比较的配件（列头） */
    private List<ComparePartVO> parts;

    /** 参数行 */
    private List<CompareRowVO> rows;
}
