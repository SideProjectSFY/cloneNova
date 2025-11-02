package com.ssafy.clonenova.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 이메일 중복 확인 응답 DTO
 * 
 * REST-API-SPEC.md: 1.12 이메일 중복 확인
 */
@Getter
@Builder
public class CheckEmailResponseDTO {
    /**
     * 확인한 이메일
     */
    private String email;
    
    /**
     * 사용 가능 여부 (true: 사용 가능, false: 이미 사용 중)
     */
    private Boolean available;
}
