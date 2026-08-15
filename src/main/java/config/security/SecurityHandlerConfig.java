package config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.security.Principal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import common.result.Result;
import common.result.ResultCode;
import common.until.IpUtil;
import jakarta.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class SecurityHandlerConfig {

    /**
     * 自定义认证失败处理器
     */
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            log.warn("[认证失败] ip: {}, 原因: {}", IpUtil.getRealIp(request), ResultCode.UNAUTHORIZED.getMessage());

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Result<?> result = Result.error(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage());
            writeJson(response, result);
        };
    }

    /**
     * 自定义授权失败处理器
     */
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            Principal principal = request.getUserPrincipal();
            String username = principal != null ? principal.getName() : "unknown";
            log.warn("[授权失败] ip: {}, 用户: {}, 原因: {}", IpUtil.getRealIp(request), username,
                    ResultCode.FORBIDDEN.getMessage());

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Result<?> result = Result.error(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage());
            writeJson(response, result);

        };
    }

    /**
     * 输出JSON响应
     */
    private void writeJson(HttpServletResponse response, Result<?> result) {
        try (PrintWriter writer = response.getWriter()) {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            writer.write(mapper.writeValueAsString(result));
            writer.flush();
        } catch (Exception e) {
            log.error("输出JSON失败", e);
        }
    }
}
