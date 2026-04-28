package com.example.jwt_practice.auth.service;

import com.example.jwt_practice.auth.dao.RefreshTokenDao;
import com.example.jwt_practice.auth.dao.UserDao;
import com.example.jwt_practice.auth.dto.LoginRequest;
import com.example.jwt_practice.auth.dto.SignupRequest;
import com.example.jwt_practice.auth.dto.TokenResponse;
import com.example.jwt_practice.auth.entity.RefreshToken;
import com.example.jwt_practice.auth.entity.User;
import com.example.jwt_practice.auth.exception.AuthErrorCode;
import com.example.jwt_practice.auth.security.JwtProvider;
import com.example.jwt_practice.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDao userDao;
    private final RefreshTokenDao refreshTokenDao;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void signup(SignupRequest signupRequest) {
        // loginId는 case-insensitive 정책: 저장/조회 모두 소문자로 정규화
        String normalizedLoginId = signupRequest.getLoginId().toLowerCase(Locale.ROOT);
        if (userDao.existsByLoginId(normalizedLoginId)) {
            throw new BusinessException(AuthErrorCode.DUPLICATE_LOGIN_ID);
        }
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        userDao.save(new User(normalizedLoginId, encodedPassword, "USER"));
    }

    // 로그인 검증 및 토큰 생성
    public TokenResponse login(LoginRequest loginRequest) {

        String normalizedLoginId = loginRequest.getLoginId().toLowerCase(Locale.ROOT);
        User user = userDao.findByUserId(normalizedLoginId);
        if (user == null) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = rotateRefreshToken(user.getId());
        return new TokenResponse(accessToken, refreshToken);
    }

    /** RT Rotation 적용: RT 사용 시마다 새 RT 발급 + 기존 RT는 DB에서 삭제.
     *
     * TODO: 재사용 탐지 강화 (옵션)
     *  - RefreshToken 엔티티에 revoked 플래그 또는 family_id 추가
     *  - 폐기된 RT가 다시 사용되면 해당 user의 모든 RT 일괄 무효화 (탈취 신호로 활용)
     *  - 만료된 RT cleanup용 스케줄러 추가 (예: @Scheduled로 일 1회 만료 row 정리)
     */
    public TokenResponse reissue (String refreshToken) {
        if(!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken savedToken = refreshTokenDao.findByToken(refreshToken);
        if (savedToken == null) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userDao.findById(savedToken.getUserId());
        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String newRefreshToken = rotateRefreshToken(user.getId());

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout (String refreshToken) {
        if (refreshToken == null) {
            return;
        }
        RefreshToken savedToken = refreshTokenDao.findByToken(refreshToken);
        if (savedToken == null) {
            return;
        }
        refreshTokenDao.deleteByUserId(savedToken.getUserId());
    }

    /** 기존 RT를 폐기하고 새 RT를 발급/저장하여 반환.
     *  login(중복 로그인 방지)과 reissue(RT Rotation) 둘 다 사용.
     */
    private String rotateRefreshToken(Long userId) {
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);
        refreshTokenDao.deleteByUserId(userId);
        refreshTokenDao.save(new RefreshToken(
                userId,
                newRefreshToken,
                LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
        return newRefreshToken;
    }
}
