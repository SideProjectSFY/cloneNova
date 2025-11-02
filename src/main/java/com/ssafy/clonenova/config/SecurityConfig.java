package com.ssafy.clonenova.config;

import com.ssafy.clonenova.auth.filter.CustomAccessDeniedHandler;
import com.ssafy.clonenova.auth.filter.CustomAuthenticationEntryPoint;
import com.ssafy.clonenova.auth.filter.JwtAuthenticationFilter;
import com.ssafy.clonenova.auth.provider.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public SecurityConfig(
            @Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            JwtAuthenticationProvider jwtAuthenticationProvider
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

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
                    "/api/v1/auth/**",      // 인증 관련 API (회원가입, 로그인, 로그아웃 등)
                    "/swagger-ui/**",       // Swagger UI
                    "/v3/api-docs/**",      // OpenAPI 문서
                    "/swagger-ui.html",     // Swagger UI (레거시)
                    "/h2-console/**"        // H2 Console (개발 환경)
                ).permitAll()
                
                // 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // H2 Console을 위한 X-Frame-Options 설정
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
            )
            
            // JWT 인증 필터 추가 (UsernamePasswordAuthenticationFilter 앞에 추가)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 예외 처리 핸들러 추가
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            )
        ;

        return http.build();
    }

    /**
     * AuthenticationManager 빈 등록
     * 
     * JwtAuthenticationProvider를 사용하여 인증을 처리합니다.
     * 
     * @param authenticationConfiguration AuthenticationConfiguration
     * @return AuthenticationManager
     * @throws Exception 설정 오류
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        // JwtAuthenticationProvider를 사용하는 ProviderManager 생성
        return new ProviderManager(List.of(jwtAuthenticationProvider));
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

