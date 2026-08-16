package module.template.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ParamTemplateDetailVO {

    // 主键ID（雪花算法，业务生成）
    private Long templateId;

    // 外键：part_category.cat_id
    private Long catId;

    // 模板名称
    private String templateName;

    // 模板版本号
    private String version;

    // 模板生效日期
    private LocalDateTime effectDate;

    // 失效日期，为空代表永久有效
    private LocalDateTime expireDate;

    // 状态：0-草稿 1-待审核 2-已发布
    private Integer status;

    // 模板备注说明
    private String remark;

    // 审批人用户ID，草稿状态为空
    private Long auditUserId;

    // 审批人名称
    private String auditUserName;
}
