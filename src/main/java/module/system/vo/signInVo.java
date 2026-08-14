package module.system.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户注册vo
 */
@Data
public class signInVo {

    /**
     * 真实姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String realName;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$" , message = "手机号格式不正确，请输入正确的格式")
    private String phone;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[A-Za-z0-9._-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-])*+\\.[A-Za-z0-9-]{2,6}$",
            message = "邮箱格式不正确，请输入正确格式")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[A-Za-z0-9]{8,12}",
            message = "密码格式不对，请包含字母+数字，长度为8-12位")
    private String password;

    /**
     * 用户权限
     */
    private Integer userType;
}
