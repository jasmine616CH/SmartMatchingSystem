package module.system.aop;

import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.until.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import module.system.annotation.OperateLog;
import module.system.entity.SysOperateLog;
import module.system.service.SysOperateLogService;


@RequiredArgsConstructor 
@Component 
@Aspect 
public class OperateLogAspect {
    
    @Resource
    private SysOperateLogService sysOperateLogService;

    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(module.system.annotation.OperateLog)")
    public void operateLogPointcut() {
    }

    @Around("operateLogPointcut()")
    public Object aroundOperateLog(ProceedingJoinPoint joinPoint) throws Throwable {
        SysOperateLog log = new SysOperateLog();
        log.setOperateTime(LocalDateTime.now());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperateLog operateLog = signature.getMethod().getAnnotation(OperateLog.class);

        log.setOperateUserId(SecurityUtils.getCurrentUserId());
        log.setRealName(SecurityUtils.getCurrentRealName());
        log.setOperateDesc(operateLog.operateDesc());
        log.setOperateType(operateLog.operateType());
        log.setOperateModule(operateLog.operateModule());

        Object args = joinPoint.getArgs();
        try{
            log.setRequestParam(objectMapper.writeValueAsString(args));
        } catch (Exception e) {
            log.setRequestParam("参数序列化失败");
        }

        Object result = null;
        boolean success = true;
        String errorMsg = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            success = false;
            errorMsg = throwable.getMessage();
            throw throwable;
        } finally {
            log.setSuccess(success ? "1" : "0");
            log.setErrorMsg(errorMsg);
            try {
                log.setResponseResult(objectMapper.writeValueAsString(result));
            } catch (JsonProcessingException e) {
                log.setResponseResult("返回值序列化失败");
            }
            sysOperateLogService.saveOperateLog(log);
        }
        return result;
    }
}
