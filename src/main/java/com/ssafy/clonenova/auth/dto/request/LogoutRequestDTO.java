package com.ssafy.clonenova.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그아웃 요청 DTO
 * 
 * REST-API-SPEC.md 기반 필드:
 * - refreshToken: JWT Refresh Token
 */
@Getter
@Setter
public class LogoutRequestDTO {

    /**
     * JWT Refresh Token
     * 필수
     */
    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}
