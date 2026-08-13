package module.system.dto;

import lombok.Data;

/**
 * 用户登录dto
 */
@Data
public class loginDTO {

    /** 登录账号 */
    private String username;

    /** 密码（加密后） */
    private String password;

}
