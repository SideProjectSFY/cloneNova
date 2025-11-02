package com.ssafy.clonenova.auth.filter;

import com.ssafy.clonenova.auth.provider.JwtAuthenticationToken;
import com.ssafy.clonenova.auth.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * 
 * 모든 요청을 가로채서 JWT 토큰을 추출하고,
 * AuthenticationManager를 통해 인증을 처리합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. 요청 헤더에서 JWT 토큰 추출
        String token = extractToken(request);

        // 2. 토큰이 존재하고 유효한 경우에만 인증 처리
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            try {
                // 3. JwtAuthenticationToken 생성 (미인증 상태)
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(token);

                // 4. AuthenticationManager를 통해 인증 처리
                // → JwtAuthenticationProvider가 자동으로 호출됨
                Authentication authentication = authenticationManager.authenticate(authToken);

                // 5. SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT 인증 성공: userId={}", authentication.getName());

            } catch (Exception e) {
                log.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage());
                // 인증 실패 시 SecurityContext를 비우고 다음 필터로 진행
                SecurityContextHolder.clearContext();
            }
        }

        // 6. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 JWT 토큰 추출
     * 
     * @param request HTTP 요청
     * @return JWT 토큰 (없으면 null)
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}

