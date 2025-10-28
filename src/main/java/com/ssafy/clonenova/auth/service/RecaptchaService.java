package com.ssafy.clonenova.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class RecaptchaService {

    @Value("${recaptcha.secret.key}")
    private String secretKey;

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final double SCORE_THRESHOLD = 0.5;

    private final RestTemplate restTemplate;

    public RecaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * reCAPTCHA 토큰 검증
     * @param token reCAPTCHA 토큰
     * @param remoteIp 사용자 IP 주소
     * @return 검증 성공 여부
     */
    public boolean verifyToken(String token, String remoteIp) {
        try {
            // Google reCAPTCHA API 호출
            RecaptchaResponse response = callRecaptchaApi(token, remoteIp);
            
            if (response == null) {
                log.error("reCAPTCHA API 응답이 null입니다.");
                return false;
            }

            // 검증 결과 확인
            if (!response.isSuccess()) {
                log.warn("reCAPTCHA 검증 실패: {}", response.getErrorCodes());
                return false;
            }

            // 점수 확인 (v3에서만)
            if (response.getScore() != null && response.getScore() < SCORE_THRESHOLD) {
                log.warn("reCAPTCHA 점수 부족: {} (임계값: {})", response.getScore(), SCORE_THRESHOLD);
                return false;
            }

            log.info("reCAPTCHA 검증 성공: score={}, action={}", response.getScore(), response.getAction());
            return true;

        } catch (Exception e) {
            log.error("reCAPTCHA 검증 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Google reCAPTCHA API 호출
     */
    private RecaptchaResponse callRecaptchaApi(String token, String remoteIp) {
        try {
            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 요청 파라미터 설정
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", token);
            if (remoteIp != null && !remoteIp.isEmpty()) {
                params.add("remoteip", remoteIp);
            }

            // API 호출
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<RecaptchaResponse> response = restTemplate.postForEntity(
                    VERIFY_URL, request, RecaptchaResponse.class);

            return response.getBody();

        } catch (Exception e) {
            log.error("reCAPTCHA API 호출 실패: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * reCAPTCHA 응답 DTO
     */
    @Data
    public static class RecaptchaResponse {
        private boolean success;
        private Double score;
        private String action;
        private String challengeTs;
        private String hostname;
        
        @JsonProperty("error-codes")
        private List<String> errorCodes;

        public boolean isSuccess() {
            return success;
        }
    }
}