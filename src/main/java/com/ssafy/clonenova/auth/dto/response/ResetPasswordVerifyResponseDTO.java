package com.ssafy.clonenova.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 비밀번호 재설정 인증 응답 DTO
 * 
 * REST-API-SPEC.md: 1.13 비밀번호 재설정 인증
 */
@Getter
@Builder
public class ResetPasswordVerifyResponseDTO {
    private String email;
    private LocalDateTime changedAt;
}

