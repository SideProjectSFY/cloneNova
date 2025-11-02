package com.ssafy.clonenova.auth.provider;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * JWT 토큰을 담는 Authentication 객체
 */
@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;
    private final Object principal;

    /**
     * 미인증 토큰 생성 (인증 전)
     * 
     * @param token JWT 토큰
     */
    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.principal = null;
        setAuthenticated(false);
    }

    /**
     * 인증된 토큰 생성 (인증 후)
     * 
     * @param principal 사용자 정보 (UserDetails)
     * @param token JWT 토큰
     * @param authorities 권한 목록
     */
    public JwtAuthenticationToken(Object principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}

