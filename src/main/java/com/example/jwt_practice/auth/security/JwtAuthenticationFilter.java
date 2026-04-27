package com.example.jwt_practice.auth.security;

import com.example.jwt_practice.auth.exception.AuthErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * [Client] ── 요청 (Authorization: Bearer <token> 포함했다는 전제)
 *    │
 *    ▼
 * [JwtAuthenticationFilter.doFilterInternal]
 *    │  resolveToken(request)
 *    │
 *    ├─ ① 토큰 없음 (비로그인 요청)
 *    │     → chain.doFilter() 그대로 통과 (메모 X)
 *    │     → 비보호 자원이면 통과 / 보호 자원이면 ExceptionTranslationFilter가 EntryPoint 호출
 *    │
 *    ├─ ② 토큰 있음 + 검증 성공
 *    │     → SecurityContext에 인증 정보 저장
 *    │     → chain.doFilter() 통과 → 컨트롤러 도달
 *    │
 *    ├─ ③ 토큰 있음 + ExpiredJwtException
 *    │     → request.setAttribute(ERROR_CODE_ATTRIBUTE, EXPIRED_TOKEN)  ← 메모
 *    │     → SecurityContextHolder.clearContext()                       ← 안전장치
 *    │     → chain.doFilter() 통과 (응답 직접 안 씀!)
 *    │     → SecurityContext 비어 있음 → 보호 자원이면 EntryPoint가 메모 보고 401 응답
 *    │
 *    └─ ④ 토큰 있음 + 그 외 예외 (위조/형식오류)
 *          → request.setAttribute(ERROR_CODE_ATTRIBUTE, INVALID_TOKEN)  ← 메모
 *          → SecurityContextHolder.clearContext()
 *          → chain.doFilter() 통과
 *          → 보호 자원이면 EntryPoint가 메모 보고 401 응답
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);        // 비로그인 요청
            return;
        }

        try {
            if (jwtProvider.validateToken(token)) {
                Long userId = jwtProvider.getUserId(token);
                String role = jwtProvider.getRole(token);

                // JWT 검증이 완료 후 요청 주인에 대한 값을 Spring Security에 전달
                // 1) Spring Security에서 인증 정보를 담는 객체 (인증된 유저 식별자, credentials(비밀번호), 권한 목록)
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                // 2) SecurityContext에 인증 정보 저장 (Controller에서 @AuthenticationPrincipal로 인증 정보 꺼낼 목적)
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ExpiredJwtException e) {
            // 만료된 토큰: 메모만 남기고 chain 통과 → JwtAuthenticationEntryPoint가 응답
            request.setAttribute(JwtAuthenticationEntryPoint.ERROR_CODE_ATTRIBUTE, AuthErrorCode.EXPIRED_TOKEN);
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            // 위조/형식오류 토큰: 메모만 남기고 chain 통과
            request.setAttribute(JwtAuthenticationEntryPoint.ERROR_CODE_ATTRIBUTE, AuthErrorCode.INVALID_TOKEN);
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
