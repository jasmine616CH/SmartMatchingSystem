package common.until;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    public static String getRealIp(HttpServletRequest request) {
        // 优先读取代理转发头
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String xRealIp = request.getHeader("X-Real-IP");

        String ip = null;
        if (StringUtils.hasText(xForwardedFor)) {
            // X-Forwarded-For 格式：客户端ip,代理1ip,代理2ip... 第一个就是真实IP
            ip = xForwardedFor.split(",")[0].trim();
        } else if (StringUtils.hasText(xRealIp)) {
            ip = xRealIp.trim();
        } else {
            ip = request.getRemoteAddr();
        }

        // 兼容本地调试
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}