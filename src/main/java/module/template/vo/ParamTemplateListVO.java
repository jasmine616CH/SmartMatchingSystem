package module.template.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ParamTemplateListVO {

    // 主键ID（雪花算法，业务生成）
    private Long templateId;

    // 模板名称
    private String templateName;

    // 模板版本号
    private String version;

    // 失效日期，为空代表永久有效
    private LocalDateTime expireDate;

    // 状态：0-草稿 1-待审核 2-已发布
    private Integer status;
}
