package module.system.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import common.enums.OperateModule;
import common.enums.OperateType;

@Target(ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    /**
     * 操作日志描述
     */
    String operateDesc() default "";

    /**
     * 业务操作类型枚举
     */
    OperateType operateType() default OperateType.QUERY;

    /**
     * 操作模块
     */
    OperateModule operateModule() default OperateModule.DICT;
}
