package module.system.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户登录dto
 */
@Data
public class LoginDTO {

    /** 登录账号 */
    private String username;

    /** 密码 */
    @Pattern(regexp = "^[A-Za-z0-9]{8,12}",
            message = "密码格式不对，请包含字母+数字，长度为8-12位")
    private String password;

}
