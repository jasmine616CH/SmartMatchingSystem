package module.scheme.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ParamTemplate {

    /** 主键ID（雪花算法，业务生成） */
    private Long templateId;

    /** 外键：配件分类ID，绑定三级配件分类 */
    private Long catId;

    /** 模板名称，如永磁同步电机标准参数模板 */
    private String templateName;

    /** 模板版本号，如V1.0 */
    private String version;

    /** 模板生效日期 */
    private LocalDate effectDate;

    /** 失效日期，为空代表永久有效 */
    private LocalDate expireDate;

    /** 状态：0-草稿 1-待审核 2-已发布 */
    private Integer status;

    /** 审批人用户ID，草稿状态为空 */
    private Long auditUserId;

    /** 模板备注说明 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

}
