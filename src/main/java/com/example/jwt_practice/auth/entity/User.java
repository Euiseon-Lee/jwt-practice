package com.example.jwt_practice.auth.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class User {

    private Long id;
    private String loginId;
    private String password;
    private String role;
    private String createdAt;
    private String updatedAt;

    public User(String loginId, String password, String role) {
        this.loginId = loginId;
        this.password = password;
        this.role = role;
    }
}