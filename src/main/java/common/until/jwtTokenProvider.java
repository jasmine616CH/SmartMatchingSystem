package common.until;

import common.exception.BusinessException;
import common.result.ResultCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class jwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * 获取成功Token有效期（毫秒）
     */
    @Getter
    @Value("${jwt.access-token-ttl}")
    private long accessTokenTtl;

    public String generateAccessToken(String userName , String userType){
        Date now = new Date();

        return Jwts.builder()
                .subject(userName)
                .claim("userType" , userType)
                .expiration(new Date(now.getTime() + accessTokenTtl))
                .issuedAt(now)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     *从Token中提取userName
     */
    public String getUserNameFromToken(String token){
        return paresClaims(token).getSubject();
    }

    /**
     * 从Token中提取userType
     */
    public Integer getPermissionFromToken(String token){
        return paresClaims(token).get("userTypr" , Integer.class);
    }

    /**
     * 验证Token是否有效
     */
    public boolean isTokenValid(String token){
        try {
            paresClaims(token);
            return true;
        } catch (JwtException e ){
            return false;
        }
    }

    /**
     * 统一解析Token载荷（私有公共方法）
     */
    private Claims paresClaims(String token){

        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (ExpiredJwtException e){
            throw new BusinessException(ResultCode.TOKEN_EXPIRED);
        } catch (SignatureException e){
            throw new BusinessException(ResultCode.TOKEN_SIGNATURE_ERROR);
        } catch (MalformedJwtException e){
            throw new BusinessException(ResultCode.TOKEN_MALFORMED);
        } catch (Exception e){
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        return claims;

    }

    /**
     * 生成签名密钥
     */
    public SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

}
