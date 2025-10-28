package com.ssafy.clonenova.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // Refresh Token 저장 (7일)
    public void saveRefreshToken(String userId, String token) {
        String key = "refreshToken:" + userId;
        redisTemplate.opsForValue().set(key, token, Duration.ofDays(7));
        log.info("Refresh Token 저장 완료: userId={}", userId);
    }

    // Refresh Token 조회
    public String getRefreshToken(String userId) {
        String key = "refreshToken:" + userId;
        return redisTemplate.opsForValue().get(key);
    }

    // Refresh Token 삭제 (로그아웃)
    public void deleteRefreshToken(String userId) {
        String key = "refreshToken:" + userId;
        redisTemplate.delete(key);
        log.info("Refresh Token 삭제 완료: userId={}", userId);
    }

    // 이메일 인증 코드 저장 (5분)
    public void saveEmailVerificationCode(String email, String code) {
        String key = "emailVerify:" + email;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));
        log.info("이메일 인증 코드 저장 완료: email={}", email);
    }

    // 이메일 인증 코드 검증
    public boolean verifyEmailCode(String email, String inputCode) {
        String key = "emailVerify:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);
        boolean isValid = inputCode.equals(storedCode);
        
        if (isValid) {
            // 인증 성공 시 코드 삭제
            redisTemplate.delete(key);
            log.info("이메일 인증 성공: email={}", email);
        } else {
            log.warn("이메일 인증 실패: email={}", email);
        }
        
        return isValid;
    }

    // 비밀번호 재설정 토큰 저장 (30분)
    public void savePasswordResetToken(String token, String userId) {
        String key = "passwordReset:" + token;
        redisTemplate.opsForValue().set(key, userId, Duration.ofMinutes(30));
        log.info("비밀번호 재설정 토큰 저장 완료: userId={}", userId);
    }

    // 비밀번호 재설정 토큰으로 userId 조회
    public String getUserIdByResetToken(String token) {
        String key = "passwordReset:" + token;
        return redisTemplate.opsForValue().get(key);
    }

    // 비밀번호 재설정 토큰 삭제
    public void deletePasswordResetToken(String token) {
        String key = "passwordReset:" + token;
        redisTemplate.delete(key);
        log.info("비밀번호 재설정 토큰 삭제 완료: token={}", token);
    }

    // 로그인 실패 횟수 증가
    public void incrementLoginAttempts(String email) {
        String key = "loginAttempts:" + email;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofMinutes(15));
        log.info("로그인 실패 횟수 증가: email={}", email);
    }

    // 로그인 실패 횟수 조회
    public int getLoginAttempts(String email) {
        String key = "loginAttempts:" + email;
        String attempts = redisTemplate.opsForValue().get(key);
        return attempts != null ? Integer.parseInt(attempts) : 0;
    }

    // 로그인 실패 횟수 초기화
    public void resetLoginAttempts(String email) {
        String key = "loginAttempts:" + email;
        redisTemplate.delete(key);
        log.info("로그인 실패 횟수 초기화: email={}", email);
    }

    // 로그인 시도 제한 확인 (5회 초과 시 차단)
    public boolean isLoginBlocked(String email) {
        int attempts = getLoginAttempts(email);
        return attempts >= 5;
    }

    // TTL 조회
    public long getTtl(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // 키 존재 여부 확인
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
