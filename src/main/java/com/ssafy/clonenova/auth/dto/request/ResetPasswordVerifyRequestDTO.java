package com.ssafy.clonenova.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 비밀번호 재설정 인증 요청 DTO
 * 
 * REST-API-SPEC.md: 1.13 비밀번호 재설정 인증
 */
@Getter
@Setter
public class ResetPasswordVerifyRequestDTO {

    /**
     * 비밀번호 재설정 토큰
     * 필수
     */
    @NotBlank(message = "토큰은 필수입니다.")
    private String token;

    /**
     * 새로운 비밀번호
     * 필수, 8-20자, 영문+숫자+특수문자 조합
     */
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8-20자여야 합니다."
    )
    private String newPassword;
}
