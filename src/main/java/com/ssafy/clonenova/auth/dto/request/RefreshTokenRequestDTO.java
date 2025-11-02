package com.ssafy.clonenova.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 액세스 토큰 갱신 요청 DTO
 * 
 * REST-API-SPEC.md: 1.11 액세스 토큰 갱신
 */
@Getter
@Setter
public class RefreshTokenRequestDTO {

    /**
     * JWT Refresh Token
     * 필수
     */
    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}
