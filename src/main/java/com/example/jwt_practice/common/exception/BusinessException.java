package com.example.jwt_practice.common.exception;

import lombok.Getter;

/** 에러 발생 시 이벤트 전달자
 *  - Service/Controller가 비즈니스 규칙 위반을 던지는 수단
 *  - ErrorCode를 포함하기 때문에 "어떤 규칙 위반인지"를 함께 전달
 *  - RuntimeException을 상속해 throws 선언 전파를 피함
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}