package module.system.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddAccountDTO {

    /**
     * 雪花ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$" , message = "手机号格式不正确，请输入正确的格式")
    private String phone;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 密码
     */
    @Pattern(regexp = "^[A-Za-z0-9]{8,12}",
            message = "密码格式不对，请包含字母+数字，长度为8-12位")
    private String password;


}
