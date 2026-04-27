package com.example.jwt_practice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** spring-boot-starter-webmvc는 Jackson autoconfiguration을 트리거하지 않아 ObjectMapper 빈을 직접 등록.
 *  - findAndRegisterModules()로 클래스패스의 Jackson 모듈(JSR-310 datetime 등)을 자동 발견
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
