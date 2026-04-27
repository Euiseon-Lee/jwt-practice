package com.example.jwt_practice.auth.security;

import com.example.jwt_practice.common.exception.ErrorCode;
import com.example.jwt_practice.common.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** JWT 보안 핸들러들이 공통으로 쓰는 ErrorResponse JSON 직렬화 헬퍼
 *  - JwtAuthenticationEntryPoint (401)
 *  - JwtAccessDeniedHandler (403)
 *  컨트롤러 단(ControllerExceptionHandler)은 ResponseEntity로 반환하므로 이 헬퍼 불필요.
 */
@Component
@RequiredArgsConstructor
public class JwtErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, ErrorCode errorCode, String path) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ErrorResponse.of(errorCode, path));
    }
}
