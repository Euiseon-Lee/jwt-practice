package com.example.jwt_practice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor         // 모든 필드를 받는 생성자 생성 (AT, RT 모두 필수로 필요하기 때문)
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
}