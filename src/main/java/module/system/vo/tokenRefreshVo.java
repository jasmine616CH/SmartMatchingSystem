package module.system.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 刷新令牌响应
 */
@Data
@Builder
public class tokenRefreshVo {

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    private Long refreshExpiresIn;

    private String tokenType;

}
