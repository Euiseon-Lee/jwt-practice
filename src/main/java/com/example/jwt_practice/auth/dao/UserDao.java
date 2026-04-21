package com.example.jwt_practice.auth.dao;

import com.example.jwt_practice.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    User findByUserId(String loginId);
    User findById(Long id);
    boolean existsByLoginId(String loginId);
    void save(User user);
}