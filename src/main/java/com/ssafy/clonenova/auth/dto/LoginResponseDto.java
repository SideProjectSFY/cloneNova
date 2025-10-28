package com.ssafy.clonenova.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Long refreshExpiresIn;
    private UserInfoDto user;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfoDto {
        private String userId;
        private String email;
        private String name;
        private String nickname;
        private String avatar;
        private String provider;
    }
}