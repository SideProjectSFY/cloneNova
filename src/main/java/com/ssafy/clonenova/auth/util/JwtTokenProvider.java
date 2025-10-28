package com.ssafy.clonenova.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration:3600000}") // 1시간 (밀리초)
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}") // 7일 (밀리초)
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Access Token 생성
    public String generateAccessToken(String userId, String email, String nickname) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("nickname", nickname);
        claims.put("type", "access");

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(String userId) {
        String jti = java.util.UUID.randomUUID().toString();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("jti", jti);

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Token에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            log.error("JWT 토큰에서 사용자 ID 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    // Token에서 Claims 추출
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("JWT 토큰에서 Claims 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    // Token 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    // Token 만료 여부 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return true;
            }
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            log.error("JWT 토큰 만료 확인 실패: {}", e.getMessage());
            return true;
        }
    }

    // Refresh Token에서 JTI 추출
    public String getJtiFromRefreshToken(String refreshToken) {
        try {
            Claims claims = getClaimsFromToken(refreshToken);
            if (claims == null) {
                return null;
            }
            return claims.get("jti", String.class);
        } catch (JwtException e) {
            log.error("Refresh Token에서 JTI 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    // Token 타입 확인
    public String getTokenType(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return null;
            }
            return claims.get("type", String.class);
        } catch (JwtException e) {
            log.error("Token 타입 확인 실패: {}", e.getMessage());
            return null;
        }
    }

    // Access Token 만료 시간 반환 (초)
    public long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }

    // Refresh Token 만료 시간 반환 (초)
    public long getRefreshTokenExpirationInSeconds() {
        return refreshTokenExpiration / 1000;
    }
}