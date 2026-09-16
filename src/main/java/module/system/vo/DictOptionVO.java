package module.system.vo;

import lombok.Data;

/**
 * 字典下拉选项
 * <p>供「按 dictCode 取启用选项」的对外接口使用。
 */
@Data
public class DictOptionVO {

    /** 数据库存储真实值 */
    private String value;

    /** 前端下拉展示文字 */
    private String label;

    /** 排序号，前端如需自行排序可用 */
    private Integer sort;
}
