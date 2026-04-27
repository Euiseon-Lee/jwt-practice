package com.example.jwt_practice.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** 클라이언트에 전달할 출력 포맷 (JSON)
 *  - ControllerExceptionHandler가 ErrorCode와 요청 path를 조합해서 생성함
 *  - 프론트와의 응답 계약, ErrorCode 필드로 클라이언트가 분기 가능
 */
@Getter
@Builder
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    private final int status;
    private final String code;
    private final String message;
    private final String path;

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(path)
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(message)
                .path(path)
                .build();
    }
}