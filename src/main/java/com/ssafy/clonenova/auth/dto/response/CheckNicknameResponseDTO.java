package com.ssafy.clonenova.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 닉네임 중복 확인 응답 DTO
 * 
 * REST-API-SPEC.md: 1.13 닉네임 중복 확인
 */
@Getter
@Builder
public class CheckNicknameResponseDTO {
    /**
     * 확인한 닉네임
     */
    private String nickname;
    
    /**
     * 사용 가능 여부 (true: 사용 가능, false: 이미 사용 중)
     */
    private Boolean available;
}
