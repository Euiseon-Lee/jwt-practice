package com.example.jwt_practice.auth.exception;

import com.example.jwt_practice.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** auth 도메인 에러 코드
 * - [AUTH-401-1] 존재하지 않는 유저 또는 비번 틀린 경우
 * - [AUTH-401-2] 유효하지 않은 RT로 POST /auth/reissue 호출하는 경우
 * - [AUTH-401-3] 쿠키 없이 POST /auth/reissue 호출하는 경우
 * - [AUTH-401-4] 만료된 AT로 보호 API 호출하는 경우
 * - [AUTH-401-5] 위조/형식 오류 AT로 보호 API 호출하는 경우
 * - [AUTH-403-1] 권한 부족 자원 호출 (ROLE_USER가 /admin/** 등)
 * - [AUTH-409]   이미 있는 loginId로 POST /auth/signup 호출하는 경우
 */
@Getter
public enum AuthErrorCode implements ErrorCode {

    // 401 Unauthorized
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-401-1", "Invalid login id or password."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-2", "Invalid or expired refresh token."),
    MISSING_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-3", "Refresh token cookie is missing."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-4", "Expired access token."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-5", "Invalid access token."),

    // 403 Forbidden
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-403-1", "Access denied."),

    // 409 Conflict
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "AUTH-409", "Login id already exists.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    AuthErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
