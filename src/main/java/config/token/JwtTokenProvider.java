package config.token;

import common.exception.BusinessException;
import common.result.ResultCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Access Token 有效期（毫秒）
     */
    @Getter
    @Value("${jwt.access-token-ttl}")
    private long accessTokenTtl;

    /**
     * 生成 AccessToken
     * 
     * @param userName 用户名
     * @param userType 用户类型标识
     * @return jwt字符串
     */
    public String generateAccessToken(String userName, String userType) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userName)
                .claim("userType", userType)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenTtl))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从Token中提取用户名
     */
    public String getUserNameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 从Token中提取用户类型
     */
    public String getUserTypeFromToken(String token) {
        return parseClaims(token).get("userType", String.class);
    }

    // 只校验签名、过期、格式（不绑定用户，不安全，尽量少用）
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | BusinessException e) {
            log.warn("token校验失败:{}", e.getMessage());
            return false;
        }
    }

    // 推荐使用！绑定用户，安全校验
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = parseClaims(token);
            String tokenUsername = claims.getSubject();
            return tokenUsername.equals(userDetails.getUsername());
        } catch (JwtException | BusinessException e) {
            log.warn("Token校验失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 统一解析Token载荷
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new BusinessException(ResultCode.TOKEN_SIGNATURE_ERROR);
        } catch (MalformedJwtException e) {
            throw new BusinessException(ResultCode.TOKEN_MALFORMED);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }
    }

    /**
     * 获取加密密钥
     */
    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}