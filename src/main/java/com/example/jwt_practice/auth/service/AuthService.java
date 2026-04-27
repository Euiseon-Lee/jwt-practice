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

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDao userDao;
    private final RefreshTokenDao refreshTokenDao;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void signup(SignupRequest signupRequest) {
        if (userDao.existsByLoginId(signupRequest.getLoginId())) {
            throw new BusinessException(AuthErrorCode.DUPLICATE_LOGIN_ID);
        }
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        userDao.save(new User(signupRequest.getLoginId(), encodedPassword, "USER"));
    }

    // 로그인 검증 및 토큰 생성
    public TokenResponse login(LoginRequest loginRequest) {

        User user = userDao.findByUserId(loginRequest.getLoginId());
        if (user == null) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        refreshTokenDao.deleteByUserId(user.getId());

        RefreshToken refreshTokenEntity = new RefreshToken(
                user.getId(), refreshToken, LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        refreshTokenDao.save(refreshTokenEntity);
        return new TokenResponse(accessToken, refreshToken);
    }

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

        return new TokenResponse(newAccessToken, refreshToken);
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
}