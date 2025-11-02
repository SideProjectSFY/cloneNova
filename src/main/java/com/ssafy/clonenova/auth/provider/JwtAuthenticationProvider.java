package com.ssafy.clonenova.auth.provider;

import com.ssafy.clonenova.auth.util.JwtTokenProvider;
import com.ssafy.clonenova.users.entity.User;
import com.ssafy.clonenova.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * JWT 토큰 인증을 처리하는 AuthenticationProvider
 * 
 * AuthenticationManager가 이 Provider를 통해 JWT 토큰 인증을 처리합니다.
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // JwtAuthenticationToken에서 토큰 추출
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
        String token = jwtAuthToken.getToken();

        // 1. 토큰 타입 확인 (Access Token만 허용)
        String tokenType = jwtTokenProvider.getTokenType(token);
        if (!"access".equals(tokenType)) {
            throw new BadCredentialsException("Invalid token type: " + tokenType);
        }

        // 2. 토큰에서 사용자 ID 추출
        String userId = jwtTokenProvider.getUserId(token);

        // 3. 사용자 조회 및 계정 상태 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!user.isActive()) {
            throw new BadCredentialsException("비활성화된 계정입니다.");
        }

        // 4. UserDetails 생성
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getId())
                .password("")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        // 5. 인증된 Authentication 객체 반환
        return new JwtAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // JwtAuthenticationToken 타입만 처리
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

