package module.system.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 信息修改DTO
 */
@Data
public class PasswordUpdateDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 原密码
     */
    @Pattern(regexp = "^[A-Za-z0-9]{8,12}",
            message = "密码格式不对，请包含字母+数字，长度为8-12位")
    private String oldPassword;

    /**
     * 新密码
     */
    @Pattern(regexp = "^[A-Za-z0-9]{8,12}",
            message = "密码格式不对，请包含字母+数字，长度为8-12位")
    private String newPassword;

}
