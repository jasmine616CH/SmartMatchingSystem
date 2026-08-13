package module.system.vo;

import lombok.Data;

import java.lang.reflect.Array;

/**
 * 返回给前端的菜单栏Vo
 */
@Data
public class menuRouteVo {

    /**
     *路由路径
     */
    private String path;

    /**
     * 前端组件路径
     */
    private String component;

    /**
     * 子菜单数组
     */
    private Array children;

    /**
     * 菜单名字
     */
    private String name;

    /**
     *是否隐藏
     */
    private boolean hidden;

    /**
     * 元数据对象
     */
    @Data
    public static class meta{

        /**
         *菜单图标
         */
        private final String icon;

        /**
         *菜单标题
         */
        private final String title;

    }


}
