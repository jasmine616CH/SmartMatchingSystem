package module.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字典类型表 (sys_dict_type)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysDictType {

    /** 主键ID（雪花算法，业务生成） */
    @TableId
    private Long dictTypeId;

    /**
     * 字典大类编码：SYS-系统通用类, PARAM-参数模板配置类, ACCESSORY-配件业务字典类, RULE-规则与选型类
     * <p>该列 NOT NULL，实体上必须有，否则插入时会违反非空约束。
     */
    private String dictCategory;

    /** 字典分类中文名称 */
    private String dictName;

    /** 字典唯一编码，关联参数模板 */
    private String dictCode;

    /** 状态：0-停用 1-启用 */
    private Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
