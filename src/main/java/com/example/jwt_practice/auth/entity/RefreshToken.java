package com.example.jwt_practice.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private Long id;
    private Long userId;
    private String token;
    private String expiresAt;
    private String createdAt;

    public RefreshToken(Long userId, String token, String expiresAt) {
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
    }
}