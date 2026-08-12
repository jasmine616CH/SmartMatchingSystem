package module.part.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 配件接口信息表 (part_interface)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartInterface {

    /** 主键ID（雪花算法，业务生成） */
    private Long interfaceId;

    /** 外键：part_info.part_id 所属配件 */
    private Long partId;

    /** 接口类型：机械安装/电气供电/CAN/CAN FD/以太网 */
    private String interfaceType;

    /** 接口规格文字描述 */
    private String connectSpec;

    /** 针脚定义详情 */
    private String pinDefine;

    /** 通信DBC协议版本 */
    private String dbcVersion;

    /** 接口补充说明 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
