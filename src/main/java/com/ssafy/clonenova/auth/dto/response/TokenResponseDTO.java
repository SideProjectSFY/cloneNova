package com.ssafy.clonenova.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 액세스 토큰 갱신 응답 DTO
 * 
 * REST-API-SPEC.md: 1.11 액세스 토큰 갱신
 */
@Getter
@Builder
public class TokenResponseDTO {
    /**
     * 새로 발급된 Access Token
     */
    private String accessToken;
    
    /**
     * 토큰 타입 (Bearer)
     */
    private String tokenType;
    
    /**
     * Access Token 만료 시간 (초 단위)
     */
    private Long expiresIn;
}
