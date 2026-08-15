package module.system.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 用户登录vo
 */
@Data
@Builder
public class LoginVO {

    /**
     * 姓名
     */
    private String realName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 账户类型
     */
    private String userType;

    /**
     * 菜单栏
     */
    private List<menuRouteVo> menuRoute;


    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     *访问令牌过期时间
     */
    private Long expiresIn;

    /**
     *刷新令牌过期时间
     */
    private Long refreshExpiresIn;

    /**
     * token类型
     */
    private String tokenType;

}
