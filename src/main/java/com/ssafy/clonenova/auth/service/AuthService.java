package com.ssafy.clonenova.auth.service;

import com.ssafy.clonenova.auth.dto.LoginRequestDto;
import com.ssafy.clonenova.auth.dto.LoginResponseDto;
import com.ssafy.clonenova.auth.dto.RegisterRequestDto;
import com.ssafy.clonenova.auth.dto.RegisterResponseDto;
import com.ssafy.clonenova.auth.util.JwtTokenProvider;
import com.ssafy.clonenova.users.entity.Avatar;
import com.ssafy.clonenova.users.entity.Role;
import com.ssafy.clonenova.users.entity.User;
import com.ssafy.clonenova.users.entity.UserRole;
import com.ssafy.clonenova.users.repository.AvatarRepository;
import com.ssafy.clonenova.users.repository.RoleRepository;
import com.ssafy.clonenova.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AvatarRepository avatarRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final RecaptchaService recaptchaService;

    /**
     * 회원가입
     */
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto requestDto, String recaptchaToken, String remoteIp) {
        // 1. reCAPTCHA 검증
        if (!recaptchaService.verifyToken(recaptchaToken, remoteIp)) {
            throw new IllegalArgumentException("reCAPTCHA 검증에 실패했습니다.");
        }

        // 2. 이메일 중복 확인
        if (userRepository.existsByEmailAndDeletedAtIsNull(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 3. 닉네임 중복 확인
        if (userRepository.existsByNicknameAndDeletedAtIsNull(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 4. 기본 아바타 조회 (ID: 1)
        Avatar defaultAvatar = avatarRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("기본 아바타를 찾을 수 없습니다."));

        // 5. 사용자 생성
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .name(requestDto.getName())
                .nickname(requestDto.getNickname())
                .provider("local")
                .avatar(defaultAvatar)
                .emailVerified(false)
                .build();

        // 6. 기본 역할 부여 (USER)
        Role userRole = roleRepository.findByAuthority("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("USER 역할을 찾을 수 없습니다."));
        
        user.addRole(userRole);

        // 7. 사용자 저장
        User savedUser = userRepository.save(user);

        log.info("회원가입 완료: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        return RegisterResponseDto.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .nickname(savedUser.getNickname())
                .provider(savedUser.getProvider())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    /**
     * 로그인
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto, String recaptchaToken, String remoteIp) {
        // 1. reCAPTCHA 검증
        if (!recaptchaService.verifyToken(recaptchaToken, remoteIp)) {
            throw new IllegalArgumentException("reCAPTCHA 검증에 실패했습니다.");
        }

        // 2. 로그인 시도 제한 확인
        if (redisService.isLoginBlocked(requestDto.getEmail())) {
            throw new IllegalArgumentException("로그인 시도 횟수를 초과했습니다. 15분 후 다시 시도해주세요.");
        }

        // 3. 사용자 조회
        User user = userRepository.findByEmailAndDeletedAtIsNull(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 4. 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            // 로그인 실패 횟수 증가
            redisService.incrementLoginAttempts(requestDto.getEmail());
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 5. 로그인 성공 시 실패 횟수 초기화
        redisService.resetLoginAttempts(requestDto.getEmail());

        // 6. JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getNickname());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 7. Refresh Token을 Redis에 저장
        redisService.saveRefreshToken(user.getId(), refreshToken);

        log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationInSeconds())
                .refreshExpiresIn(jwtTokenProvider.getRefreshTokenExpirationInSeconds())
                .user(LoginResponseDto.UserInfoDto.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar() != null ? user.getAvatar().getFilePath() : null)
                        .provider(user.getProvider())
                        .build())
                .build();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String userId) {
        // Refresh Token 삭제
        redisService.deleteRefreshToken(userId);
        log.info("로그아웃 완료: userId={}", userId);
    }

    /**
     * 이메일 중복 확인
     */
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    /**
     * 닉네임 중복 확인
     */
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNicknameAndDeletedAtIsNull(nickname);
    }

    /**
     * 사용자 ID로 사용자 조회
     */
    public Optional<User> findUserById(String userId) {
        return userRepository.findById(userId);
    }

    /**
     * 이메일로 사용자 조회
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email);
    }
}