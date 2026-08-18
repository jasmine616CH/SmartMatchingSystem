package common.until;

import common.enums.UserType;
import common.exception.BusinessException;
import common.result.ResultCode;
import config.security.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 获取当前用户登录信息工具
 */
@Component
public class SecurityUtils {

    /**
     * 私有构造
     */
    private SecurityUtils() {
    }

    /**
     * 获取当前认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户
     */
    public static LoginUser getLogInUser() {
        Authentication auth = getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof LoginUser)) {
            throw new BusinessException(ResultCode.USER_NOT_LOGIN);
        }
        return (LoginUser) auth.getPrincipal();
    }

    /**
     * 获取当前登录用户名
     * 
     * @return 用户名
     */
    public static String getCurrentUserName() {
        return getLogInUser().getUsername();
    }

    public static boolean isStudent() {
        return getLogInUser().getAuthorities().stream()
                .anyMatch(auth -> UserType.APPROVER.getAuthority().equals(auth.getAuthority()));
    }

    /**
     * 清除安全上下文
     */
    public static void cleanContext() {
        SecurityContextHolder.clearContext();
    }

}
