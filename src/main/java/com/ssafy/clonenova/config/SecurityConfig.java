package com.ssafy.clonenova.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Spring Security 필터 체인 설정
     * 
     * 주요 설정:
     * - JWT 기반 인증 (Stateless)
     * - CSRF 비활성화 (JWT 사용)
     * - 세션 비활성화
     * - 인증 불필요 경로 설정 (회원가입, 로그인, Swagger 등)
     * - JWT 필터 추가 (추후 구현)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (JWT 사용 시 불필요)
            .csrf(csrf -> csrf.disable())
            
            // 세션 비활성화 (Stateless JWT 방식)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 요청 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 공개 API (인증 불필요)
                .requestMatchers(
                    "/api/v1/auth/**",      // 인증 관련 API (회원가입, 로그인 등)
                    "/swagger-ui/**",       // Swagger UI
                    "/v3/api-docs/**",      // OpenAPI 문서
                    "/swagger-ui.html"      // Swagger UI (레거시)
                ).permitAll()
                
                // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // TODO: JWT 인증 필터 추가
            // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // TODO: 예외 처리 핸들러 추가
            // .exceptionHandling(exception -> exception
            //     .authenticationEntryPoint(...)
            //     .accessDeniedHandler(...)
            // )
        ;

        return http.build();
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈
     * 
     * BCrypt 해싱 알고리즘 사용
     * - Salt 자동 생성
     * - 암호화 강도: 기본값 (10 rounds)
     * 
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

