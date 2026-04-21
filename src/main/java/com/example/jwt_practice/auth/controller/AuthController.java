package com.example.jwt_practice.auth.controller;

import com.example.jwt_practice.auth.dto.AccessTokenResponse;
import com.example.jwt_practice.auth.dto.LoginRequest;
import com.example.jwt_practice.auth.dto.SignupRequest;
import com.example.jwt_practice.auth.dto.TokenResponse;
import com.example.jwt_practice.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String REFRESH_COOKIE_PATH = "/auth";
    private static final Duration REFRESH_COOKIE_MAX_AGE = Duration.ofDays(7);

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokens = authService.login(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(tokens.getRefreshToken()).toString())
                .body(new AccessTokenResponse(tokens.getAccessToken()));
    }

    @PostMapping("/reissue")
    public ResponseEntity<AccessTokenResponse> reissue(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new RuntimeException("Missing refresh token cookie.");
        }
        TokenResponse tokens = authService.reissue(refreshToken);
        return ResponseEntity.ok(new AccessTokenResponse(tokens.getAccessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken
    ) {
        authService.logout(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildExpiredRefreshCookie().toString())
                .build();
    }

    private ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(REFRESH_COOKIE_MAX_AGE)
                .build();
    }

    private ResponseCookie buildExpiredRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(0)
                .build();
    }
}
