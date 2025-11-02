package com.ssafy.clonenova.common.exception;

import com.ssafy.clonenova.auth.exception.RecaptchaException;
import com.ssafy.clonenova.common.dto.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 검증 실패 시 처리
     * 
     * @param ex MethodArgumentNotValidException
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultVO<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String message = errors.values().iterator().hasNext() 
                ? errors.values().iterator().next() 
                : "입력값이 유효하지 않습니다.";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResultVO.<Map<String, String>>builder()
                        .code(4001)
                        .message(message)
                        .data(errors)
                        .build());
    }

    /**
     * reCAPTCHA 검증 실패 처리
     * 
     * @param ex RecaptchaException
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(RecaptchaException.class)
    public ResponseEntity<ResultVO<Void>> handleRecaptchaException(RecaptchaException ex) {
        log.warn("reCAPTCHA 검증 실패: {}", ex.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResultVO.<Void>builder()
                        .code(4001)
                        .message(ex.getMessage())
                        .data(null)
                        .build());
    }

    /**
     * 모든 예외 처리 (최종 처리)
     * 
     * @param ex Exception
     * @return 500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultVO<Void>> handleAllExceptions(Exception ex) {
        log.error("예상치 못한 오류 발생", ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultVO.<Void>builder()
                        .code(5000)
                        .message("서버 오류가 발생했습니다.")
                        .data(null)
                        .build());
    }
}
