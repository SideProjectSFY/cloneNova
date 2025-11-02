package com.ssafy.clonenova.auth.controller;

import com.ssafy.clonenova.auth.dto.request.LoginRequestDTO;
import com.ssafy.clonenova.auth.dto.request.LogoutRequestDTO;
import com.ssafy.clonenova.auth.dto.request.RegisterRequestDTO;
import com.ssafy.clonenova.auth.dto.response.LoginResponseDTO;
import com.ssafy.clonenova.auth.dto.response.RegisterResponseDTO;
import com.ssafy.clonenova.auth.service.AuthService;
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
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 API 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthService authService;
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

    /**
     * 로그인
     * 
     * REST-API-SPEC.md: 1.2 로그인
     * 
     * @param request 로그인 요청 DTO
     * 테스트용: reCAPTCHA 검증 주석 처리
     * @param recaptchaToken reCAPTCHA 토큰 (헤더)
     * @return 로그인 결과 (Access Token, Refresh Token, 사용자 정보)
     */
    @PostMapping("/login")
    public ResponseEntity<ResultVO<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request
            // 테스트용: reCAPTCHA 검증 주석 처리
            // @RequestHeader(value = "X-Recaptcha-Token", required = true) String recaptchaToken,
            // HttpServletRequest httpRequest
    ) {
        // 테스트용: reCAPTCHA 검증 주석 처리
        // String remoteIp = httpRequest.getRemoteAddr();
        // recaptchaService.verifyToken(recaptchaToken, remoteIp);

        // 로그인 처리
        LoginResponseDTO response = authService.login(request);

        // 성공 응답
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultVO.<LoginResponseDTO>builder()
                        .code(200)
                        .message("success")
                        .data(response)
                        .build());
    }

    /**
     * 로그아웃
     * 
     * REST-API-SPEC.md: 1.11 로그아웃
     * 
     * @param authorization Authorization 헤더 (Bearer {accessToken})
     * @param request 로그아웃 요청 DTO (refreshToken)
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<ResultVO<Map<String, Object>>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody LogoutRequestDTO request
    ) {
        // Authorization 헤더 확인
        if (authorization == null || authorization.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ResultVO.<Map<String, Object>>builder()
                            .code(4001)
                            .message("인증이 필요합니다.")
                            .data(null)
                            .build());
        }

        // 로그아웃 처리
        authService.logout(authorization, request.getRefreshToken());

        // 응답 데이터 생성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("loggedOutAt", LocalDateTime.now());

        // 성공 응답
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResultVO.<Map<String, Object>>builder()
                        .code(200)
                        .message("success")
                        .data(responseData)
                        .build());
    }
}
