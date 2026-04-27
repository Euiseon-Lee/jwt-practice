package com.example.jwt_practice.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 컨트롤러 단에서 발생한 예외를 ErrorResponse JSON으로 변환
 *
 *  ── 전체 그림 ──
 *
 *  [컨트롤러 단 예외]                    [필터 단 보안 예외]
 *         │                                    │
 *         ▼                                    ├─ 인증 실패 → JwtAuthenticationEntryPoint
 *  ControllerExceptionHandler                  │    └─ 권한 부족 → JwtAccessDeniedHandler
 *         │                                    │
 *         │                                    │
 *         └───── ErrorResponse JSON 생성 ───────┘
 *
 *  ── 이 클래스의 동작 ──
 *
 *                    [Service / Controller]
 *                            │
 *                            │ throw
 *                            ▼
 *                   ┌─────────────────────┐
 *                   │  BusinessException  │ ──has──▶ ┌──────────┐
 *                   └─────────────────────┘          │ ErrorCode│
 *                            │                       └──────────┘
 *                            │ Spring이 가로챔               ▲
 *                            ▼                              │
 *                   ┌───────────────────────┐               │
 *                   │ ControllerExceptionHandler│────reads──────┘
 *                   └───────────────────────┘
 *                            │
 *                            │ builds
 *                            ▼
 *                   ┌─────────────────────┐
 *                   │   ErrorResponse     │
 *                   └─────────────────────┘
 *                            │
 *                            ▼
 *                     [JSON 응답 → Client]
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    // 비즈니스 로직에서 의도적으로 던진 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("[BusinessException] {} {} - {}", errorCode.getCode(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, ex.getMessage(), request.getRequestURI()));
    }

    // @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse(CommonErrorCode.INVALID_REQUEST.getMessage());
        log.warn("[ValidationException] {} - {}", request.getRequestURI(), message);
        return ResponseEntity
                .status(CommonErrorCode.INVALID_REQUEST.getStatus())
                .body(ErrorResponse.of(CommonErrorCode.INVALID_REQUEST, message, request.getRequestURI()));
    }

    // 그 외 예상하지 못한 모든 예외 (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {
        log.error("[UnhandledException] {}", request.getRequestURI(), ex);
        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI()));
    }
}