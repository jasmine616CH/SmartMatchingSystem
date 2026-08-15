package config.token;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Token过期时间
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secret;
    private Long accessTokenExpire = 1800L; // 30分钟
    private Long refreshTokenExpire = 604800L; // 7天
}