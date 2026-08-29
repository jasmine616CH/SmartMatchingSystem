package module.system.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class tokenRefreshVo {

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    private Long refreshExpiresIn;

    private String tokenType;

}
