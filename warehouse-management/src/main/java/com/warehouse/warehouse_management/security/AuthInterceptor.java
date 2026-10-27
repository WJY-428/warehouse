package com.warehouse.warehouse_management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.warehouse_management.common.Result;
import com.warehouse.warehouse_management.common.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 登录校验拦截器：除白名单外，所有接口都要求 Authorization: Bearer &lt;token&gt;。
 * 校验通过后把 userId / username 放进 request attribute，方便 controller 取用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 浏览器跨域预检请求直接放行
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return reject(response);
        }

        try {
            Claims claims = jwtUtil.parse(header.substring(BEARER_PREFIX.length()).trim());
            request.setAttribute("userId", Long.valueOf(claims.getSubject()));
            request.setAttribute("username", claims.get("username", String.class));
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("token 校验失败：{}", e.getMessage());
            return reject(response);
        }
    }

    private boolean reject(HttpServletResponse response) throws java.io.IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(ResultCode.UNAUTHORIZED)));
        return false;
    }
}
