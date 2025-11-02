package com.ssafy.clonenova.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Google reCAPTCHA 검증 API 응답 DTO
 */
@Getter
@Setter
public class RecaptchaResponseDTO {
    
    /**
     * 검증 성공 여부
     */
    @JsonProperty("success")
    private boolean success;
    
    /**
     * 신뢰도 점수 (0.0 ~ 1.0)
     * v3에서만 사용
     */
    @JsonProperty("score")
    private Double score;
    
    /**
     * Action 이름 (프론트엔드에서 지정)
     */
    @JsonProperty("action")
    private String action;
    
    /**
     * 검증 시간
     */
    @JsonProperty("challenge_ts")
    private String challengeTs;
    
    /**
     * 호스트명
     */
    @JsonProperty("hostname")
    private String hostname;
    
    /**
     * 에러 코드 목록 (선택)
     */
    @JsonProperty("error-codes")
    private List<String> errorCodes;
}
