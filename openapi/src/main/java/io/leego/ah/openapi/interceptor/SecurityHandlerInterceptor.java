package io.leego.ah.openapi.interceptor;

import io.leego.ah.openapi.annotation.Privileged;
import io.leego.ah.openapi.config.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.Annotation;

/**
 * @author Leego Yih
 */
@Component
public class SecurityHandlerInterceptor implements HandlerInterceptor {
    public static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN_TYPE = "Bearer";
    private static final Logger logger = LoggerFactory.getLogger(SecurityHandlerInterceptor.class);
    private final SecurityProperties securityProperties;

    public SecurityHandlerInterceptor(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod hm)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return false;
        }
        boolean accessible = authorize(request, response, hm) || isPrivileged(hm);
        if (!accessible) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[{}#{}] {} {} {} {}",
                    hm.getBeanType().getSimpleName(),
                    hm.getMethod().getName(),
                    accessible ? "\033[32mAuthorized \033[0m" : "\033[31mUnauthorized\033[0m",
                    request.getMethod(),
                    request.getRequestURI(),
                    getIp(request));
        }
        return accessible;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        clear(request, response);
    }

    public boolean authorize(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        int prefixLen = TOKEN_TYPE.length() + 1;
        String secretKey = securityProperties.getSecretKey();
        String token = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(token)) {
            return (token.length() == secretKey.length() + prefixLen)
                    && (token.indexOf(secretKey, prefixLen) > 0);
        }
        return false;
    }

    public void clear(HttpServletRequest request, HttpServletResponse response) {
    }

    public boolean isPrivileged(HandlerMethod handler) {
        return hasAnnotation(handler, Privileged.class);
    }

    protected boolean hasAnnotation(HandlerMethod handler, Class<? extends Annotation> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(handler.getMethod(), annotationType)
                || AnnotatedElementUtils.hasAnnotation(handler.getBeanType(), annotationType);
    }

    protected <A extends Annotation> A getAnnotation(HandlerMethod handler, Class<A> annotationType) {
        A a = AnnotatedElementUtils.findMergedAnnotation(handler.getMethod(), annotationType);
        if (a != null) {
            return a;
        }
        return AnnotatedElementUtils.findMergedAnnotation(handler.getBeanType(), annotationType);
    }

    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"};

    private static String getIp(HttpServletRequest request) {
        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.indexOf(',') > 0) {
                    return ip.split(",")[0];
                } else {
                    return ip;
                }
            }
        }
        return request.getRemoteAddr();
    }
}
