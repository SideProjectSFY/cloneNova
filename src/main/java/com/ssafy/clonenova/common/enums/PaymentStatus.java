package com.ssafy.clonenova.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 결제 상태 (payments.status)
 * READY: 결제 준비
 * PAID: 결제 승인 완료
 * FAILED: 결제 실패
 * CANCELLED: 결제 취소(승인 후 취소 포함)
 * UNKNOWN: 역직렬화 시 알 수 없는 값 대응용 기본 상태
 */
public enum PaymentStatus {
    READY, PAID, FAILED, CANCELLED, UNKNOWN;

    @JsonCreator
    public static PaymentStatus from(String value) {
        if (value == null) return UNKNOWN;
        try {
            return PaymentStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return UNKNOWN;
        }
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
