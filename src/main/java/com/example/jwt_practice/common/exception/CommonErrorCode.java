package com.example.jwt_practice.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** 도메인에 속하지 않는 공통 에러 코드
 * - [COMMON-400] @Valid 검증 실패
 * - [COMMON-500] 예상 못한 예외 발생한 경우
 */
@Getter
public enum CommonErrorCode implements ErrorCode {

    // 400 Bad Request
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-400", "Invalid request."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-500", "Internal server error.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    CommonErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
