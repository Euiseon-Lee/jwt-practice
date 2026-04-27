package com.example.jwt_practice.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey secretKey;      // JWT 서명에서 사용할 비밀키
    private final long accessTokenExpiration;   // Access Token 만료 시간 (ms)
    private final long refreshTokenExpiration;  // Refresh Token 만료 시간 (ms)

    public JwtProvider (
            @Value("${jwt.secret}") String secret
            , @Value("${jwt.access-token-expiration}") long accessTokenExpiration
            , @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));   // secret(String)을 UTF-8 바이트 배열로 변환 후 HMAC-SHA 알고리즘용 SecretKey로 변환
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken (Long userId, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))       // Payload의 sub 클레임에 String으로 변환한 userId 저장
                .claim("role", role)             // Payload에 커스텀 클레임 추가 (API 요청에 사용할 목적)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey)                    // secretKey로 서명 (Signature 생성)
                .compact()                              // 모든 설정을 합쳐서 문자열로 직렬화
                ;
    }

    public String generateRefreshToken (Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey)
                .compact()
                ;
    }

    public Long getUserId (String token) {
        return Long.parseLong(getClaims(token).getSubject());       // 토큰을 파싱해서 Payload(Claims) 꺼낸 뒤 subject 클레임 값 추출, String → Long 변환
    }

    public String getRole (String token) {
        return getClaims(token).get("role", String.class);  // Payload에서 "role" 키 값 꺼낸 뒤 String.class로 변환
    }

    /**
     * 토큰 검증
     *  → getClaims() 호출
     *  → 서명 검증 → 만료 확인
     *  → 정상: true / 위조: false / 만료: throw
     * @param token String
     * @return boolean
     */
    public boolean validateToken (String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {       // 위조, 형식오류
            return false;
        }
    }

    private Claims getClaims (String token) {
        return Jwts.parser()
                .verifyWith(secretKey)          // secretKey로 Signature 검증, 위조 토큰일 경우 여기서 예외 발생
                .build()                        // parser 생성
                .parseSignedClaims(token)       // token 문자열 파싱, 만료된 토큰이면 여기서 ExpiredJwtException 발생
                .getPayload();
    }
}