package module.system.vo;

import java.util.List;

import lombok.Data;

/**
 * 按字典编码取到的字典内容
 * <p>
 * 比裸数组多带一个 dictName，前端下拉的标题可以直接用，不必再查一次字典类型。
 */
@Data
public class DictDetailVO {

    /** 字典唯一编码 */
    private String dictCode;

    /** 字典分类中文名称 */
    private String dictName;

    /** 启用状态的字典项，按 sort 升序 */
    private List<DictOptionVO> items;
}
