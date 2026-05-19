package com.wye.aspect;

import com.wye.common.RequireRole;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class RoleAspect {

    @Around("@annotation(com.wye.common.RequireRole) || @within(com.wye.common.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        Integer role = (Integer) request.getAttribute("role");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 优先使用方法级别的注解，其次使用类级别的注解
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
        }

        if (requireRole != null && role != null) {
            int[] allowedRoles = requireRole.value();
            boolean hasRole = Arrays.stream(allowedRoles).anyMatch(r -> r == role);
            if (!hasRole) {
                HttpServletResponse response = attributes.getResponse();
                if (response != null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"权限不足\"}");
                    return null;
                }
            }
        }

        return joinPoint.proceed();
    }
}
