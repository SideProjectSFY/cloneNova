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
}
