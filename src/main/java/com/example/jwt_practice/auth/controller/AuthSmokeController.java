package com.example.jwt_practice.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** 인증/권한 흐름 검증용 보호 엔드포인트 (smoke test).
 *  - GET /api/test/needAuth        : 인증 필요 (anyRequest().authenticated())
 *  - GET /admin/test/needAdminRole : ROLE_ADMIN 필요 (/admin/** hasRole("ADMIN"))
 *  새 작업의 인증/권한 동작을 빠르게 점검하는 1차 검증 용도로 장기 유지
 *  (예: A-1 도입 검증, A-2 RT 회전 검증, 프론트 interceptor 동작 확인).
 */
@RestController
public class AuthSmokeController {

    @GetMapping("/api/test/needAuth")
    public Map<String, Object> needAuth(@AuthenticationPrincipal Long userId) {
        return Map.of("userId", userId);
    }

    @GetMapping("/admin/test/needAdminRole")
    public Map<String, String> needAdminRole() {
        return Map.of("message", "admin access OK");
    }
}