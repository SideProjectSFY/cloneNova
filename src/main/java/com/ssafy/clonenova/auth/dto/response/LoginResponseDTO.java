package com.ssafy.clonenova.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 응답 DTO
 * 
 * REST-API-SPEC.md 기반 필드:
 * - accessToken: JWT Access Token
 * - refreshToken: JWT Refresh Token
 * - tokenType: 토큰 타입 (Bearer)
 * - expiresIn: Access Token 만료 시간 (초)
 * - refreshExpiresIn: Refresh Token 만료 시간 (초)
 * - user: 사용자 정보
 */
@Getter
@Setter
@Builder
public class LoginResponseDTO {

    /**
     * JWT Access Token
     */
    private String accessToken;

    /**
     * JWT Refresh Token
     */
    private String refreshToken;

    /**
     * 토큰 타입 (항상 "Bearer")
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Access Token 만료 시간 (초 단위)
     * 기본값: 3600초 (1시간)
     */
    @Builder.Default
    private Long expiresIn = 3600L;

    /**
     * Refresh Token 만료 시간 (초 단위)
     * 기본값: 604800초 (7일)
     */
    @Builder.Default
    private Long refreshExpiresIn = 604800L;

    /**
     * 사용자 정보
     */
    private UserInfo user;

    /**
     * 사용자 정보 내부 클래스
     */
    @Getter
    @Setter
    @Builder
    public static class UserInfo {
        /**
         * 사용자 ID (UUID)
         */
        private String userId;

        /**
         * 이메일 주소
         */
        private String email;

        /**
         * 실명
         */
        private String name;

        /**
         * 닉네임
         */
        private String nickname;

        /**
         * 프로필 이미지 URL
         */
        private String avatar;

        /**
         * 계정 타입 (local, google, kakao)
         */
        private String provider;
    }
}
