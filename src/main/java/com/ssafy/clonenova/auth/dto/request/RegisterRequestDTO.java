package com.ssafy.clonenova.auth.dto.request;

import com.ssafy.clonenova.users.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 회원가입 요청 DTO
 * 
 * REST-API-SPEC.md 기반 필드:
 * - email: 이메일 주소 (UNIQUE)
 * - password: 비밀번호 (8-20자, 영문+숫자+특수문자)
 * - name: 실명 (2-50자)
 * - nickname: 닉네임 (2-20자, 영문/한글/숫자, UNIQUE)
 */
@Getter
@Setter
public class RegisterRequestDTO {

    /**
     * 이메일 주소 (로그인 ID 겸용)
     * 필수, 이메일 형식, 최대 255자
     */
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 255, message = "이메일은 최대 255자까지 입력 가능합니다.")
    private String email;

    /**
     * 비밀번호 (평문)
     * 필수, 8-20자, 영문+숫자+특수문자 조합
     */
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8-20자여야 합니다.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    /**
     * 실명
     * 필수, 2-50자
     */
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 50, message = "이름은 2-50자여야 합니다.")
    private String name;

    /**
     * 닉네임
     * 필수, 2-20자, 영문/한글/숫자, UNIQUE
     */
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2-20자여야 합니다.")
    @Pattern(
        regexp = "^[a-zA-Z가-힣0-9]+$",
        message = "닉네임은 영문, 한글, 숫자만 사용 가능합니다."
    )
    private String nickname;

    /**
     * DTO를 User Entity로 변환
     * 
     * @param passwordEncoder 비밀번호 암호화를 위한 PasswordEncoder
     * @return 생성된 User 엔티티
     */
    public User toEntity(PasswordEncoder passwordEncoder) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(this.password);
        
        // User.create() 팩토리 메서드 사용
        // id, createdAt, avatarId는 @PrePersist에서 자동 설정
        return User.create(this.email, encodedPassword, this.name, this.nickname);
    }
}
