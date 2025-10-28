package com.ssafy.clonenova.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 환불 상태 (refunds.status)
 * READY: 환불 요청/준비
 * PAID: 환불(입금) 완료
 * FAILED: 환불 처리 실패
 * CANCELLED: 환불 요청 취소
 * UNKNOWN: 역직렬화 시 알 수 없는 값 대응용 기본 상태
 */
public enum RefundStatus {
    READY, PAID, FAILED, CANCELLED, UNKNOWN;

    @JsonCreator
    public static RefundStatus from(String value) {
        if (value == null) return UNKNOWN;
        try {
            return RefundStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return UNKNOWN;
        }
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
