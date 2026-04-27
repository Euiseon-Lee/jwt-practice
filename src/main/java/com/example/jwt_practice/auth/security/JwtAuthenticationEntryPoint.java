package com.example.jwt_practice.auth.security;

import com.example.jwt_practice.auth.exception.AuthErrorCode;
import com.example.jwt_practice.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** 필터 단 인증 실패(401) 핸들러
 *  - JwtAuthenticationFilter가 request attribute(ERROR_CODE_ATTRIBUTE)에 ErrorCode를 메모해두면 그걸 꺼내 응답
 *  - 메모가 없으면 (= 토큰 자체가 없는 비로그인 케이스 등) INVALID_TOKEN을 기본값으로 사용
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String ERROR_CODE_ATTRIBUTE = "jwtAuthErrorCode";

    private final JwtErrorResponseWriter writer;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        ErrorCode errorCode = (ErrorCode) request.getAttribute(ERROR_CODE_ATTRIBUTE);
        if (errorCode == null) {
            errorCode = AuthErrorCode.INVALID_TOKEN;
        }
        writer.write(response, errorCode, request.getRequestURI());
    }
}