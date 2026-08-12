package module.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 菜单路由表 (sys_menu)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysMenu {

    /** 主键ID（雪花算法，业务生成） */
    private Long menuId;

    /** 外键：sys_menu.menu_id，0为顶级目录 */
    private Long parentId;

    /** 菜单展示名称 */
    private String menuName;

    /** 前端页面路由地址 */
    private String path;

    /** 前端vue组件文件路径 */
    private String component;

    /** 接口/按钮权限标识，后端鉴权使用 */
    private String permissionKey;

    /** 菜单类型：0-目录 1-页面 2-按钮 */
    private Integer menuType;

    /** 菜单前端展示排序号 */
    private Integer sort;

    /** 状态：0-隐藏 1-正常显示 */
    private Integer status;

    /** 记录创建时间 */
    private LocalDateTime createTime;

    /** 记录更新时间 */
    private LocalDateTime updateTime;
}
