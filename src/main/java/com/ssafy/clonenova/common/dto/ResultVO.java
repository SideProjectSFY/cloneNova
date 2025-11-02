package com.ssafy.clonenova.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 API 응답 래퍼 클래스
 * 
 * @param <T> 응답 데이터 타입
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO<T> {
    /**
     * 상태 코드
     * - 200: 성공
     * - 4000: 필수값 누락
     * - 4001: 유효하지 않은 입력값
     * - 4002: 타입 오류 / Too Many Requests
     * - 4003: 엔티티 없음
     * - 4004: 중복 데이터
     */
    private int code;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * 응답 데이터
     */
    private T data;
}
