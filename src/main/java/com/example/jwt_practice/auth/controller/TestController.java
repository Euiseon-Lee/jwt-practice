package com.example.jwt_practice.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** [임시] A-1 (필터단 보안 예외 응답 통일) 검증용 보호 엔드포인트.
 *  - GET /api/test/needAuth : 인증 필요 (anyRequest().authenticated())
 *  - GET /admin/test/needAdminRole   : ROLE_ADMIN 필요 (/admin/** hasRole("ADMIN"))
 *  검증 끝나면 삭제 또는 실제 도메인으로 발전.
 */
@RestController
public class TestController {

    @GetMapping("/api/test/needAuth")
    public Map<String, Object> needAuth(@AuthenticationPrincipal Long userId) {
        return Map.of("userId", userId);
    }

    @GetMapping("/admin/test/needAdminRole")
    public Map<String, String> adminTest() {
        return Map.of("message", "admin access OK");
    }
}