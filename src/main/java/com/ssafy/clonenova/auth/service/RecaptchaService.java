package com.ssafy.clonenova.auth.service;

import com.ssafy.clonenova.auth.dto.response.RecaptchaResponseDTO;
import com.ssafy.clonenova.auth.exception.RecaptchaException;
import com.ssafy.clonenova.config.RecaptchaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

/**
 * Google reCAPTCHA v3 검증 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RecaptchaService {

    private final RecaptchaProperties recaptchaProperties;

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final double SCORE_THRESHOLD = 0.5; // 기본 임계값

    private final RestTemplate restTemplate;

    /**
     * reCAPTCHA 토큰 검증
     * 
     * @param token reCAPTCHA 토큰
     * @param remoteIp 사용자 IP 주소
     * @return 검증 성공 여부
     * @throws RecaptchaException 검증 실패 시
     */
    public boolean verifyToken(String token, String remoteIp) {
        if (token == null || token.isEmpty()) {
            throw new RecaptchaException("reCAPTCHA 토큰이 없습니다.");
        }

        try {
            // 1. 요청 파라미터 구성
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", recaptchaProperties.getSecret().getKey());
            params.add("response", token);
            if (remoteIp != null && !remoteIp.isEmpty()) {
                params.add("remoteip", remoteIp);
            }

            // 2. HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // 3. Google API 호출
            ResponseEntity<RecaptchaResponseDTO> response = restTemplate.postForEntity(
                    VERIFY_URL,
                    request,
                    RecaptchaResponseDTO.class
            );

            RecaptchaResponseDTO body = response.getBody();

            // 4. 검증 결과 확인
            if (body == null) {
                log.error("reCAPTCHA API 응답이 null입니다.");
                throw new RecaptchaException("reCAPTCHA 검증 서비스를 일시적으로 사용할 수 없습니다.");
            }

            if (!body.isSuccess()) {
                log.warn("reCAPTCHA 검증 실패 - Error Codes: {}", body.getErrorCodes());
                throw new RecaptchaException("reCAPTCHA 검증에 실패했습니다.");
            }

            // 5. 점수 확인 (v3)
            Double score = body.getScore();
            if (score == null || score < SCORE_THRESHOLD) {
                log.warn("reCAPTCHA 점수 부족 - Score: {}, Threshold: {}", score, SCORE_THRESHOLD);
                throw new RecaptchaException(String.format(
                        "reCAPTCHA 검증에 실패했습니다. (점수: %.2f, 필요 점수: %.2f)",
                        score != null ? score : 0.0,
                        SCORE_THRESHOLD
                ));
            }

            log.debug("reCAPTCHA 검증 성공 - Score: {}, Action: {}", score, body.getAction());
            return true;

        } catch (RecaptchaException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("reCAPTCHA API 호출 실패", e);
            throw new RecaptchaException("reCAPTCHA 서비스를 일시적으로 사용할 수 없습니다.", e);
        } catch (Exception e) {
            log.error("reCAPTCHA 검증 중 예상치 못한 오류 발생", e);
            throw new RecaptchaException("reCAPTCHA 검증 중 오류가 발생했습니다.", e);
        }
    }
}
