package module.storage.dto;

import lombok.Data;

@Data
public class PartAttachmentDTO {

    /**
     * 外键：part_info.part_id 所属配件
     */
    private Long partId;

    /**
     * 是否软删除（0-未删除，1-已删除）
     */
    private int isDeleted;

    /**
     * 关联外键：user.user_id 用户id
     */
    private Long userId;

}
