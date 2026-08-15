package module.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * token对
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 访问令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 刷新令牌过期时间（秒）
     */
    private Long refreshExpiresIn;

    /**
     * 令牌类型
     */
    @Builder.Default
    private String tokenType = "Bearer";

}
