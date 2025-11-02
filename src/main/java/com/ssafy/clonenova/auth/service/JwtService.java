package com.ssafy.clonenova.auth.service;

import com.ssafy.clonenova.auth.util.JwtTokenProvider;
import com.ssafy.clonenova.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * JWT 토큰 관리 서비스
 */
@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Access Token과 Refresh Token 생성
     * 
     * @param user 사용자 엔티티
     * @return TokenPair (Access Token, Refresh Token)
     */
    public TokenPair generateTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * 토큰 쌍을 담는 내부 클래스
     */
    public static class TokenPair {
        private final String accessToken;
        private final String refreshToken;

        public TokenPair(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserId(token);
    }

    /**
     * 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @return 유효하면 true
     */
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    /**
     * 토큰 타입 확인
     * 
     * @param token JWT 토큰
     * @return 토큰 타입 (access, refresh)
     */
    public String getTokenType(String token) {
        return jwtTokenProvider.getTokenType(token);
    }
}
