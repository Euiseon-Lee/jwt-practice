package com.example.jwt_practice.common.exception;

import org.springframework.http.HttpStatus;

/** 에러 코드의 공통 계약 (contract)
 *  - 도메인별 enum(AuthErrorCode, CommonErrorCode 등)이 이 인터페이스를 구현
 *  - BusinessException / ErrorResponse / ControllerExceptionHandler는
 *    이 타입만 알면 충분 → 새 도메인 추가 시 common 코드 수정 불필요 (OCP)
 */
public interface ErrorCode {

    HttpStatus getStatus();

    String getCode();

    String getMessage();
}
