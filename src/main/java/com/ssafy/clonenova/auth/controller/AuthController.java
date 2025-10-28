package com.ssafy.clonenova.auth.controller;

import com.ssafy.clonenova.auth.dto.LoginRequestDto;
import com.ssafy.clonenova.auth.dto.LoginResponseDto;
import com.ssafy.clonenova.auth.dto.RegisterRequestDto;
import com.ssafy.clonenova.auth.dto.RegisterResponseDto;
import com.ssafy.clonenova.auth.service.AuthService;
import com.ssafy.clonenova.auth.util.JwtTokenProvider;
import com.ssafy.clonenova.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<ApiResponse<RegisterResponseDto>> register(
            @Valid @RequestBody RegisterRequestDto requestDto,
            @Parameter(description = "reCAPTCHA 토큰") @RequestHeader("X-Recaptcha-Token") String recaptchaToken,
            HttpServletRequest request) {
        
        try {
            String remoteIp = getClientIpAddress(request);
            RegisterResponseDto responseDto = authService.register(requestDto, recaptchaToken, remoteIp);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(responseDto));
                    
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto requestDto,
            @Parameter(description = "reCAPTCHA 토큰") @RequestHeader("X-Recaptcha-Token") String recaptchaToken,
            HttpServletRequest request) {
        
        try {
            String remoteIp = getClientIpAddress(request);
            LoginResponseDto responseDto = authService.login(requestDto, recaptchaToken, remoteIp);
            
            return ResponseEntity.ok(ApiResponse.success(responseDto));
                    
        } catch (IllegalArgumentException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            log.error("로그인 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자를 로그아웃합니다.")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(description = "JWT Access Token") @RequestHeader("Authorization") String authorization) {
        
        try {
            // Authorization 헤더에서 토큰 추출
            String token = authorization.replace("Bearer ", "");
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            
            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.unauthorized("유효하지 않은 토큰입니다."));
            }
            
            authService.logout(userId);
            
            return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다.", null));
                    
        } catch (Exception e) {
            log.error("로그아웃 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복 확인", description = "이메일 사용 가능 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<EmailCheckResponse>> checkEmail(
            @Parameter(description = "확인할 이메일") @RequestParam String email) {
        
        try {
            boolean isDuplicate = authService.checkEmailDuplicate(email);
            
            EmailCheckResponse response = new EmailCheckResponse(email, !isDuplicate);
            
            return ResponseEntity.ok(ApiResponse.success(response));
                    
        } catch (Exception e) {
            log.error("이메일 중복 확인 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 닉네임 중복 확인
     */
    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<NicknameCheckResponse>> checkNickname(
            @Parameter(description = "확인할 닉네임") @RequestParam String nickname) {
        
        try {
            boolean isDuplicate = authService.checkNicknameDuplicate(nickname);
            
            NicknameCheckResponse response = new NicknameCheckResponse(nickname, !isDuplicate);
            
            return ResponseEntity.ok(ApiResponse.success(response));
                    
        } catch (Exception e) {
            log.error("닉네임 중복 확인 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalServerError("서버 오류가 발생했습니다."));
        }
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    // 응답 DTO 클래스들
    public static class EmailCheckResponse {
        public String email;
        public boolean available;
        
        public EmailCheckResponse(String email, boolean available) {
            this.email = email;
            this.available = available;
        }
    }

    public static class NicknameCheckResponse {
        public String nickname;
        public boolean available;
        
        public NicknameCheckResponse(String nickname, boolean available) {
            this.nickname = nickname;
            this.available = available;
        }
    }
}