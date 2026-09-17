package module.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PrivilegedAction;
import java.time.LocalDateTime;

/**
 * 配件附件表 (part_attachment)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartAttachment {

    /** 主键ID（雪花算法，业务生成） */
    private Long attId;

    /** 外键：part_info.part_id 所属配件 */
    private Long partId;

    /** 关联外键：user.user_id 用户id */
    private Long userId;

    /** 文件原始名称 */
    private String fileName;

    /** 文件后缀类型 pdf/dwg/stp等 */
    private String fileType;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 存储路径 */
    private String objectName;

    /** 文件上传时间 */
    private LocalDateTime uploadTime;

    /** 是否软删除（0-未删除，1-已删除） */
    private int isDeleted;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}