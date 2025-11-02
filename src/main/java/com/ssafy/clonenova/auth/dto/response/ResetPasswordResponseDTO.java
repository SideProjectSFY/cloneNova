package com.ssafy.clonenova.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 요청 응답 DTO
 * 
 * REST-API-SPEC.md: 1.12 비밀번호 재설정 요청
 */
@Getter
@Builder
public class ResetPasswordResponseDTO {
    private String email;
    private Long expiresIn;
    private LocalDateTime expiresAt;
}
