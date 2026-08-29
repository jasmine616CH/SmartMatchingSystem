package module.template.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局单位字典表（支持Indriya动态热加载）
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysUnit {

    /**
     * 主键
     */
    @TableId
    private Long unitId;

    /**
     * 唯一编码（程序用：WATT、NEWTON_METRE、KILO_WATT）
     */
    private String unitCode;

    /**
     * 页面展示符号：W、N·m、kW
     */
    private String symbol;

    /**
     * 中文名称：瓦特、牛米、千瓦
     */
    private String unitName;

    /**
     * 基准单位编码（指向自身则为根基准；kW指向WATT，kN·m指向NEWTON_METRE）
     */
    private String baseUnitCode;

    /**
     * Indriya构造表达式：WATT*1000、NEWTON*METRE、METRE/1000
     */
    private String indriyaExpr;

    /**
     * 1-系统预置（前端锁死不可删改表达式），0-业务动态新增（可删改）
     */
    private Integer isSystem;

    /**
     * 1-启用（下拉可见），0-停用（下拉隐藏）
     */
    private Integer enable;

    /**
     * 下拉排序
     */
    private Integer sort;

    /**
     * 物理量分组（可选）：LENGTH/POWER/TORQUE/FORCE/VOLTAGE/OTHER，便于前台分组展示
     */
    private String quantityType;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
