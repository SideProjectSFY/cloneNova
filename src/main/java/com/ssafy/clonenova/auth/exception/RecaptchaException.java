package com.ssafy.clonenova.auth.exception;

/**
 * reCAPTCHA 검증 실패 예외
 */
public class RecaptchaException extends RuntimeException {
    
    public RecaptchaException(String message) {
        super(message);
    }
    
    public RecaptchaException(String message, Throwable cause) {
        super(message, cause);
    }
}
