package com.ssafy.clonenova.auth.service;

import com.ssafy.clonenova.auth.dto.request.LoginRequestDTO;
import com.ssafy.clonenova.auth.dto.response.LoginResponseDTO;
import com.ssafy.clonenova.common.service.RedisService;
import com.ssafy.clonenova.users.entity.User;
import com.ssafy.clonenova.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
}
