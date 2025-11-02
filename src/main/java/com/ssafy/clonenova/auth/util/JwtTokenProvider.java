package com.ssafy.clonenova.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 생성 및 검증 유틸리티
 * 
 * REST-API-SPEC.md 기반:
 * - Access Token: 1시간 유효
 * - Refresh Token: 7일 유효
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L; // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    public JwtTokenProvider(@Value("${jwt.secret:clonenova-secret-key-for-jwt-token-generation-minimum-256-bits}") String secret) {
        // 최소 256비트 (32바이트) 키 필요
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret key는 최소 32자 이상이어야 합니다.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 생성
     * 
     * @param userId 사용자 ID (UUID)
     * @param email 이메일
     * @param nickname 닉네임
     * @return Access Token
     */
    public String generateAccessToken(String userId, String email, String nickname) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .claim("nickname", nickname)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     * 
     * @param userId 사용자 ID (UUID)
     * @return Refresh Token
     */
    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);
        String jti = UUID.randomUUID().toString(); // Token ID

        return Jwts.builder()
                .subject(userId)
                .claim("type", "refresh")
                .id(jti)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 Claims 추출
     * 
     * @param token JWT 토큰
     * @return Claims
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰에서 토큰 타입 추출
     * 
     * @param token JWT 토큰
     * @return 토큰 타입 (access, refresh)
     */
    public String getTokenType(String token) {
        return getClaims(token).get("type", String.class);
    }

    /**
     * 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @return 유효하면 true
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰 만료 시간 확인
     * 
     * @param token JWT 토큰
     * @return 만료되면 true
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
