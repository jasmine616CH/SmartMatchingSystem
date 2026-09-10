package module.part.vo;

import java.util.List;

import lombok.Data;

/**
 * 单条参数保存结果
 */
@Data
public class SaveParamResultVO {

    /** 保存后的最新完整字段列表（已按 showFlag 过滤 + 排序） */
    private List<PartParamFieldVO> fieldList;

    /** 本次因条件不成立被自动清除的脏数据（保留旧值供前端提示） */
    private List<PartParamFieldVO> clearedList;
}
