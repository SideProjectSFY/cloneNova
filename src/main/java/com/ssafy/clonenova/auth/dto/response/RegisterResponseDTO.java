package com.ssafy.clonenova.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 회원가입 응답 DTO
 */
@Getter
@Builder
public class RegisterResponseDTO {
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
     * 계정 타입 (local, google, kakao)
     */
    private String provider;
    
    /**
     * 생성 시간
     */
    private LocalDateTime createdAt;
}
