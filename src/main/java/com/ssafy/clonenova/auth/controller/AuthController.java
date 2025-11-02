package com.ssafy.clonenova.auth.controller;

import com.ssafy.clonenova.auth.dto.request.RegisterRequestDTO;
import com.ssafy.clonenova.auth.dto.response.RegisterResponseDTO;
// 테스트용: reCAPTCHA 검증 주석 처리
// import com.ssafy.clonenova.auth.service.RecaptchaService;
import com.ssafy.clonenova.common.dto.ResultVO;
import com.ssafy.clonenova.users.entity.User;
import com.ssafy.clonenova.users.repository.UserRepository;
import com.ssafy.clonenova.users.service.UserService;
// 테스트용: reCAPTCHA 검증 주석 처리
// import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// 테스트용: reCAPTCHA 검증 주석 처리
// import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 API 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    // 테스트용: reCAPTCHA 검증 주석 처리
    // private final RecaptchaService recaptchaService;

    /**
     * 회원가입
     * 
     * @param request 회원가입 요청 DTO
     * 테스트용: reCAPTCHA 검증 주석 처리
     * @param recaptchaToken reCAPTCHA 토큰 (헤더)
     * @param httpRequest HTTP 요청 (IP 주소 추출용)
     * @return 회원가입 결과
     */
    @PostMapping("/register")
    public ResponseEntity<ResultVO<RegisterResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request
            // 테스트용: reCAPTCHA 검증 주석 처리
            // @RequestHeader(value = "X-Recaptcha-Token", required = true) String recaptchaToken,
            // HttpServletRequest httpRequest
    ) {
        // 테스트용: reCAPTCHA 검증 주석 처리
        // String remoteIp = httpRequest.getRemoteAddr();
        // recaptchaService.verifyToken(recaptchaToken, remoteIp);
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ResultVO.<RegisterResponseDTO>builder()
                            .code(4004)
                            .message("이미 사용 중인 이메일입니다.")
                            .data(null)
                            .build());
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ResultVO.<RegisterResponseDTO>builder()
                            .code(4004)
                            .message("이미 사용 중인 닉네임입니다.")
                            .data(null)
                            .build());
        }

        // 사용자 생성
        User user = userService.create(request);

        // 응답 DTO 생성
        RegisterResponseDTO response = RegisterResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .build();

        // 성공 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResultVO.<RegisterResponseDTO>builder()
                        .code(200)
                        .message("success")
                        .data(response)
                        .build());
    }
}
