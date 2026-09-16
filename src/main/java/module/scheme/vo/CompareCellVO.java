package module.scheme.vo;

import java.util.List;

import lombok.Data;

/**
 * 并排比较矩阵中的一个格子：某配件在某参数上的判定结果
 */
@Data
public class CompareCellVO {

    /** 配件主键ID，用于前端按列对齐 */
    private Long partId;

    /** 参数实际取值，缺失时为 null */
    private String value;

    /** 判定态：0-满足 1-临界 2-不满足 3-缺失 */
    private Integer status;

    /** 判定态中文描述 */
    private String statusName;

    /** 未通过的规则提示（不含缺失场景），全部通过时为空 */
    private List<String> messages;
}
