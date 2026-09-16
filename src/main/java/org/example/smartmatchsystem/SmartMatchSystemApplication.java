package org.example.smartmatchsystem;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动类
 * <p>
 * 注意：本类所在包是 {@code org.example.smartmatchsystem}，而业务代码位于
 * {@code common} / {@code config} / {@code module} 三个<b>平级</b>的顶层包下，
 * 不在默认扫描范围内。因此必须显式声明 scanBasePackages，否则
 * {@code @Service} / {@code @RestController} / {@code @Configuration} 全都不会被注册，
 * 应用虽能启动但没有任何接口可用。
 * <p>
 * 同理，Mapper 需要显式 @MapperScan：MyBatis 的自动扫描只覆盖
 * AutoConfigurationPackages（即本类所在包），扫不到 module.**.mapper。
 * 这里限定 annotationClass = Mapper.class，避免把 Service 等接口误注册成 Mapper。
 */
@SpringBootApplication(scanBasePackages = {
        "org.example.smartmatchsystem",
        "common",
        "config",
        "module"
})
@MapperScan(basePackages = "module", annotationClass = Mapper.class)
public class SmartMatchSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMatchSystemApplication.class, args);
    }

}
