package module.system.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class signInDTO {

    /**
     * 主键ID（雪花算法，业务生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /** 登录账号，全局唯一 */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户权限
     */
    private Integer userType;

}
