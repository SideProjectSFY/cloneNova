package com.ssafy.clonenova.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    // 성공 응답 생성 메서드
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    // 에러 응답 생성 메서드
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    // 자주 사용되는 에러 응답들
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(4000, message);
    }

    public static <T> ApiResponse<T> invalidInput(String message) {
        return error(4001, message);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(4003, message);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return error(4004, message);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(4001, message);
    }

    public static <T> ApiResponse<T> internalServerError(String message) {
        return error(5000, message);
    }
}