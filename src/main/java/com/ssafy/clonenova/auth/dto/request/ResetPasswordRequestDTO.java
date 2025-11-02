package com.ssafy.clonenova.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 비밀번호 재설정 요청 DTO
 * 
 * REST-API-SPEC.md: 1.12 비밀번호 재설정 요청
 */
@Getter
@Setter
public class ResetPasswordRequestDTO {

    /**
     * 이메일 주소
     * 필수, 이메일 형식
     */
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 255, message = "이메일은 최대 255자까지 입력 가능합니다.")
    private String email;
}
