package com.ssafy.clonenova.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    CREATED, PENDING, PAID, FAILED, CANCELLED, UNKNOWN;

    @JsonCreator
    public static OrderStatus from(String value) {
        if (value == null) return UNKNOWN;
        try { return OrderStatus.valueOf(value.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return UNKNOWN; }
    }

    @JsonValue
    public String toValue() { return name(); } // "PAID" 형태로 직렬화
}
