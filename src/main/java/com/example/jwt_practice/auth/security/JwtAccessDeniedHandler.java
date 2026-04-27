package com.example.jwt_practice.auth.security;

import com.example.jwt_practice.auth.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** 필터 단 권한 부족(403) 핸들러
 *  - 인증은 통과했지만 자원 접근 권한이 부족한 경우 (예: ROLE_USER가 /admin/** 호출)
 *  - 단일 케이스라 항상 ACCESS_DENIED로 응답
 */
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final JwtErrorResponseWriter writer;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        writer.write(response, AuthErrorCode.ACCESS_DENIED, request.getRequestURI());
    }
}