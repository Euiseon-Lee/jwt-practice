package com.example.jwt_practice.auth.dao;

import com.example.jwt_practice.auth.entity.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenDao {
    void save(RefreshToken refreshToken);       // RT 저장
    RefreshToken findByToken(String token);     // token 기반 RT 조회
    RefreshToken findByUserId(Long userId);     // userId 기반 RT 조회
    void deleteByUserId(Long userId);         // 로그아웃 시에 RT 삭제


}