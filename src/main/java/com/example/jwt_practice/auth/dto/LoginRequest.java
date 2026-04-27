package com.example.jwt_practice.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 로그인 요청 DTO.
 *  - loginId : 필수, 4~20자
 *  - password: 필수, 8~30자
 *  검증 실패 시 ControllerExceptionHandler가 COMMON-400 ErrorResponse로 변환.
 *  메시지는 locale에 영향받지 않도록 영어로 직접 명시.
 */
@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "must not be blank")
    @Size(min = 4, max = 20, message = "size must be between {min} and {max}")
    private String loginId;

    @NotBlank(message = "must not be blank")
    @Size(min = 8, max = 30, message = "size must be between {min} and {max}")
    private String password;
}
