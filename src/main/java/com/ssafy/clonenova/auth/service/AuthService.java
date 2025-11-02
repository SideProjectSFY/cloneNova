package com.ssafy.clonenova.auth.service;

import com.ssafy.clonenova.auth.dto.request.LoginRequestDTO;
import com.ssafy.clonenova.auth.dto.request.ResetPasswordRequestDTO;
import com.ssafy.clonenova.auth.dto.request.ResetPasswordVerifyRequestDTO;
import com.ssafy.clonenova.auth.dto.response.LoginResponseDTO;
import com.ssafy.clonenova.auth.dto.response.ResetPasswordResponseDTO;
import com.ssafy.clonenova.auth.dto.response.ResetPasswordVerifyResponseDTO;
import com.ssafy.clonenova.auth.dto.response.TokenResponseDTO;
import com.ssafy.clonenova.auth.util.JwtTokenProvider;
import com.ssafy.clonenova.common.service.EmailService;
import com.ssafy.clonenova.common.service.RedisService;
import com.ssafy.clonenova.users.entity.User;
import com.ssafy.clonenova.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * 인증 서비스
 * 
 * 로그인, 로그아웃 등의 인증 관련 비즈니스 로직 처리
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인
     * 
     * REST-API-SPEC.md 비즈니스 로직:
     * 1. Redis에서 로그인 실패 횟수 확인 (5회 초과 시 15분 차단)
     * 2. 이메일로 유저 조회
     * 3. 계정 상태 확인 (ACTIVE 여부)
     * 4. 비밀번호 검증 (BCrypt compare)
     * 5. JWT Access Token 생성 (1시간 유효)
     * 6. JWT Refresh Token 생성 (7일 유효)
     * 7. Refresh Token을 Redis에 저장
     * 8. 로그인 실패 시 Redis 카운터 증가
     * 
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO
     */
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        String email = request.getEmail();

        // 1. 로그인 실패 횟수 확인
        Long loginAttempts = redisService.getLoginAttempts(email);
        if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "로그인 시도 횟수를 초과했습니다. 15분 후 다시 시도해주세요."
            );
        }

        // 2. 이메일로 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    // 로그인 실패 횟수 증가
                    redisService.incrementLoginAttempts(email);
                    return new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "이메일 또는 비밀번호가 올바르지 않습니다."
                    );
                });

        // 3. 계정 상태 확인 (deleted_at이 NULL이면 활성)
        if (!user.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "비활성화된 계정입니다. 고객센터에 문의하세요."
            );
        }

        // 4. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 로그인 실패 횟수 증가
            redisService.incrementLoginAttempts(email);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        // 5-6. JWT 토큰 생성
        JwtService.TokenPair tokenPair = jwtService.generateTokens(user);

        // 7. Refresh Token을 Redis에 저장
        redisService.saveRefreshToken(user.getId(), tokenPair.getRefreshToken());

        // 로그인 성공 시 실패 횟수 초기화
        redisService.resetLoginAttempts(email);

        // 8. 응답 DTO 생성
        LoginResponseDTO.UserInfo userInfo = LoginResponseDTO.UserInfo.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .avatar(null) // TODO: Avatar 조회 후 URL 설정
                .provider(user.getProvider())
                .build();

        return LoginResponseDTO.builder()
                .accessToken(tokenPair.getAccessToken())
                .refreshToken(tokenPair.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(3600L)
                .refreshExpiresIn(604800L)
                .user(userInfo)
                .build();
    }

    /**
     * 로그아웃
     * 
     * REST-API-SPEC.md 비즈니스 로직:
     * 1. Access Token에서 userId 추출
     * 2. Refresh Token 검증
     * 3. Redis에서 Refresh Token 삭제
     * 
     * @param accessToken Access Token (Authorization 헤더에서 추출)
     * @param refreshToken Refresh Token
     */
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        // 1. Access Token에서 userId 추출
        String userId;
        try {
            // "Bearer " 접두사 제거
            if (accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7);
            }
            userId = jwtService.getUserIdFromToken(accessToken);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "인증이 필요합니다."
            );
        }

        // 2. Refresh Token 검증
        if (!jwtService.validateToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "유효하지 않은 Refresh Token입니다."
            );
        }

        // Refresh Token의 userId와 Access Token의 userId 일치 확인
        String refreshTokenUserId = jwtService.getUserIdFromToken(refreshToken);
        if (!userId.equals(refreshTokenUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "토큰이 일치하지 않습니다."
            );
        }

        // 3. Redis에서 Refresh Token 삭제
        redisService.deleteRefreshToken(userId);
    }

    /**
     * 액세스 토큰 갱신
     * 
     * REST-API-SPEC.md 비즈니스 로직:
     * 1. Refresh Token JWT 검증 (서명, 만료 시간)
     * 2. Refresh Token 타입 확인 (type === "refresh")
     * 3. Refresh Token에서 userId 추출
     * 4. Redis에서 Refresh Token 존재 여부 확인
     * 5. 유저 정보 조회 및 계정 상태 확인
     * 6. 새로운 Access Token 발급 (1시간 유효)
     * 7. 기존 Refresh Token은 유지
     * 
     * @param refreshToken Refresh Token
     * @return 새로운 Access Token 응답 DTO
     */
    @Transactional
    public TokenResponseDTO refreshToken(String refreshToken) {
        // 1. Refresh Token JWT 검증
        if (!jwtService.validateToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "유효하지 않은 Refresh Token입니다. 다시 로그인해주세요."
            );
        }

        // 2. Refresh Token 타입 확인
        String tokenType = jwtService.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "유효하지 않은 Refresh Token입니다. 다시 로그인해주세요."
            );
        }

        // 3. Refresh Token에서 userId 추출
        String userId = jwtService.getUserIdFromToken(refreshToken);

        // 4. Redis에서 Refresh Token 존재 여부 확인
        String storedRefreshToken = redisService.getRefreshToken(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그아웃된 토큰입니다. 다시 로그인해주세요."
            );
        }

        // 5. 유저 정보 조회 및 계정 상태 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "사용자를 찾을 수 없습니다."
                ));

        if (!user.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "비활성화된 계정입니다. 고객센터에 문의하세요."
            );
        }

        // 6. 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        // 7. 기존 Refresh Token은 유지

        // 응답 DTO 생성
        return TokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    /**
     * 비밀번호 재설정 요청
     * 
     * REST-API-SPEC.md 비즈니스 로직:
     * 1. 이메일로 유저 조회
     * 2. 유저가 OAuth 계정인지 확인 (provider === 'local' 체크)
     * 3. Rate Limiting 확인 (1시간에 3회만 요청 가능)
     * 4. 임시 토큰 생성 (UUID v4)
     * 5. Redis에 토큰 저장 (30분 TTL)
     * 6. SMTP로 비밀번호 재설정 링크 이메일 전송
     * 
     * @param request 비밀번호 재설정 요청 DTO
     * @return 비밀번호 재설정 요청 응답 DTO
     */
    @Transactional
    public ResetPasswordResponseDTO requestResetPassword(ResetPasswordRequestDTO request) {
        String email = request.getEmail();

        // 1. 이메일로 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "등록되지 않은 이메일입니다."
                ));

        // 2. 유저가 OAuth 계정인지 확인 (provider === 'local' 체크)
        // provider가 null이거나 빈 문자열이면 local 계정으로 간주 (기존 데이터 호환성)
        String provider = user.getProvider();
        if (provider != null && !provider.trim().isEmpty() && !"local".equals(provider)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "소셜 로그인 계정은 비밀번호 재설정이 불가능합니다."
            );
        }

        // 3. Rate Limiting 확인 (1시간에 3회만 요청 가능)
        if (!redisService.canRequestPasswordReset(email)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "너무 많은 요청을 보냈습니다. 1시간 후 다시 시도해주세요."
            );
        }

        // 4. 임시 토큰 생성 (UUID v4)
        String token = java.util.UUID.randomUUID().toString();

        // 5. Redis에 토큰 저장 (30분 TTL)
        redisService.savePasswordResetToken(token, user.getId());

        // 6. SMTP로 비밀번호 재설정 링크 이메일 전송
        emailService.sendPasswordResetLink(email, token);

        // 7. 응답 DTO 생성
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        return ResetPasswordResponseDTO.builder()
                .email(email)
                .expiresIn(1800L) // 30분 (초 단위)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * 비밀번호 재설정 인증
     * 
     * REST-API-SPEC.md 비즈니스 로직:
     * 1. Redis에서 토큰으로 userId 조회
     * 2. 토큰 존재 및 TTL 확인 (없으면 만료된 토큰)
     * 3. 유저 정보 조회
     * 4. 새 비밀번호 유효성 검사 (DTO의 @Pattern으로 검증)
     * 5. 이전 비밀번호와 동일한지 확인
     * 6. 새 비밀번호 해싱 (BCrypt)
     * 7. 비밀번호 업데이트
     * 8. Redis에서 토큰 삭제
     * 9. 해당 유저의 모든 Refresh Token 무효화 (재로그인 강제)
     * 
     * @param request 비밀번호 재설정 인증 요청 DTO
     * @return 비밀번호 재설정 인증 응답 DTO
     */
    @Transactional
    public ResetPasswordVerifyResponseDTO verifyResetPassword(ResetPasswordVerifyRequestDTO request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        // 1. Redis에서 토큰으로 userId 조회
        String userId = redisService.getPasswordResetToken(token);
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "재설정 토큰이 만료되었습니다. 다시 요청해주세요."
            );
        }

        // 2. 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        // 3. 새 비밀번호 유효성 검사 (DTO의 @Pattern으로 이미 검증됨)

        // 4. 이전 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이전 비밀번호와 동일한 비밀번호는 사용할 수 없습니다."
            );
        }

        // 5. 새 비밀번호 해싱 (BCrypt)
        String hashedPassword = passwordEncoder.encode(newPassword);

        // 6. 비밀번호 업데이트
        user.setPassword(hashedPassword);

        // 7. Redis에서 토큰 삭제
        redisService.deletePasswordResetToken(token);

        // 8. 해당 유저의 모든 Refresh Token 무효화 (재로그인 강제)
        redisService.deleteRefreshToken(userId);

        // 9. 응답 DTO 생성
        return ResetPasswordVerifyResponseDTO.builder()
                .email(maskEmail(user.getEmail()))
                .changedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 이메일 마스킹 (보안)
     * 예: user@example.com → us**@example.com
     * 
     * @param email 원본 이메일
     * @return 마스킹된 이메일
     */
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 0) {
            return email;
        }
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (localPart.length() <= 2) {
            return localPart.substring(0, 1) + "**" + domain;
        } else {
            return localPart.substring(0, 2) + "**" + domain;
        }
    }
}
