package module.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 供应商主体表 (supplier)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    /** 主键ID（雪花算法，业务生成） */
    @TableId
    private Long supplierId;

    /** 供应商企业全称 */
    private String supplierName;

    /** 统一社会信用代码 */
    private String creditCode;

    /** 企业注册/办公地址 */
    private String address;

    /** 可供应配件品类范围 */
    private String supplyScope;

    /** 状态：0-草稿 1-待审核 2-已发布 */
    private Integer status;

    /** 外键：sys_user.user_id 审批人用户ID，草稿状态为空 */
    private Long auditUserId;

    /** 供应商备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
