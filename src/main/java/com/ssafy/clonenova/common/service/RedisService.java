package com.ssafy.clonenova.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis 서비스
 * 
 * Refresh Token, 이메일 인증 코드, 비밀번호 재설정 토큰, 로그인 실패 횟수 등을 관리
 * 
 * StringRedisTemplate 사용:
 * - Spring Boot가 자동으로 제공하는 빈
 * - 이미 String 직렬화가 설정되어 있음
 * - RedisTemplate<String, String>과 동일한 기능
 */
@RequiredArgsConstructor
@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    /**
     * Refresh Token 저장 (7일 TTL)
     * 
     * @param userId 사용자 ID (UUID)
     * @param token Refresh Token
     */
    public void saveRefreshToken(String userId, String token) {
        String key = "refreshToken:" + userId;
        redisTemplate.opsForValue().set(key, token, Duration.ofDays(7));
    }

    /**
     * Refresh Token 조회
     * 
     * @param userId 사용자 ID (UUID)
     * @return Refresh Token (없으면 null)
     */
    public String getRefreshToken(String userId) {
        String key = "refreshToken:" + userId;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Refresh Token 삭제 (로그아웃 시)
     * 
     * @param userId 사용자 ID (UUID)
     */
    public void deleteRefreshToken(String userId) {
        String key = "refreshToken:" + userId;
        redisTemplate.delete(key);
    }

    /**
     * 로그인 실패 횟수 증가
     * 
     * @param email 이메일 주소
     * @return 증가된 실패 횟수
     */
    public Long incrementLoginAttempts(String email) {
        String key = "loginAttempts:" + email;
        Long attempts = redisTemplate.opsForValue().increment(key);
        // 15분 TTL 설정 (처음 실패 시)
        if (attempts == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(15));
        }
        return attempts;
    }

    /**
     * 로그인 실패 횟수 조회
     * 
     * @param email 이메일 주소
     * @return 실패 횟수 (없으면 0)
     */
    public Long getLoginAttempts(String email) {
        String key = "loginAttempts:" + email;
        String attempts = redisTemplate.opsForValue().get(key);
        return attempts != null ? Long.parseLong(attempts) : 0L;
    }

    /**
     * 로그인 실패 횟수 초기화 (로그인 성공 시)
     * 
     * @param email 이메일 주소
     */
    public void resetLoginAttempts(String email) {
        String key = "loginAttempts:" + email;
        redisTemplate.delete(key);
    }

    /**
     * 키 존재 여부 확인
     * 
     * @param key Redis 키
     * @return 존재하면 true
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 키 삭제
     * 
     * @param key Redis 키
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // ==================== 이메일 인증 코드 관련 메서드 ====================

    /**
     * 이메일 인증 코드 저장 (5분 TTL)
     * 
     * @param email 이메일 주소
     * @param code 6자리 인증 코드
     */
    public void saveEmailVerificationCode(String email, String code) {
        String key = "emailVerify:" + email;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));
    }

    /**
     * 이메일 인증 코드 조회
     * 
     * @param email 이메일 주소
     * @return 인증 코드 (없으면 null)
     */
    public String getEmailVerificationCode(String email) {
        String key = "emailVerify:" + email;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 이메일 인증 코드 삭제 (인증 성공 시)
     * 
     * @param email 이메일 주소
     */
    public void deleteEmailVerificationCode(String email) {
        String key = "emailVerify:" + email;
        redisTemplate.delete(key);
    }

    /**
     * 인증 시도 횟수 증가 (최대 5회)
     * 
     * @param email 이메일 주소
     * @return 증가된 시도 횟수
     */
    public Long incrementVerificationAttempts(String email) {
        String key = "emailVerifyAttempts:" + email;
        Long attempts = redisTemplate.opsForValue().increment(key);
        // 5분 TTL 설정 (처음 시도 시)
        if (attempts == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(5));
        }
        return attempts;
    }

    /**
     * 인증 시도 횟수 조회
     * 
     * @param email 이메일 주소
     * @return 시도 횟수 (없으면 0)
     */
    public Long getVerificationAttempts(String email) {
        String key = "emailVerifyAttempts:" + email;
        String attempts = redisTemplate.opsForValue().get(key);
        return attempts != null ? Long.parseLong(attempts) : 0L;
    }

    /**
     * 인증 시도 횟수 초기화 (인증 성공 또는 새 코드 발급 시)
     * 
     * @param email 이메일 주소
     */
    public void resetVerificationAttempts(String email) {
        String key = "emailVerifyAttempts:" + email;
        redisTemplate.delete(key);
    }

    // ==================== 비밀번호 재설정 토큰 관련 메서드 ====================

    /**
     * 비밀번호 재설정 토큰 저장 (30분 TTL)
     * 
     * @param token 비밀번호 재설정 토큰 (UUID)
     * @param userId 사용자 ID (UUID)
     */
    public void savePasswordResetToken(String token, String userId) {
        String key = "passwordReset:" + token;
        redisTemplate.opsForValue().set(key, userId, Duration.ofMinutes(30));
    }

    /**
     * 비밀번호 재설정 토큰 조회
     * 
     * @param token 비밀번호 재설정 토큰 (UUID)
     * @return 사용자 ID (없으면 null)
     */
    public String getPasswordResetToken(String token) {
        String key = "passwordReset:" + token;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 비밀번호 재설정 토큰 삭제 (비밀번호 변경 성공 시)
     * 
     * @param token 비밀번호 재설정 토큰 (UUID)
     */
    public void deletePasswordResetToken(String token) {
        String key = "passwordReset:" + token;
        redisTemplate.delete(key);
    }

    /**
     * Rate Limiting: 비밀번호 재설정 요청 제한 (1시간에 3회)
     * 
     * @param email 이메일 주소
     * @return 요청 가능하면 true, 제한되었으면 false
     */
    public boolean canRequestPasswordReset(String email) {
        String key = "passwordResetRateLimit:" + email;
        String countStr = redisTemplate.opsForValue().get(key);
        
        if (countStr != null) {
            int count = Integer.parseInt(countStr);
            if (count >= 3) {
                return false; // 1시간 내에 이미 3회 요청했음
            }
            // 카운트 증가
            redisTemplate.opsForValue().increment(key);
        } else {
            // 처음 요청 시 키 생성 (1시간 TTL)
            redisTemplate.opsForValue().set(key, "1", Duration.ofHours(1));
        }
        return true;
    }
}
