package module.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登出请求
 */
@Data
public class TokenLogoutDTO {

    @NotBlank(message = "访问令牌不能为空")
    private String accessToken;

}
