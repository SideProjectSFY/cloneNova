package com.ssafy.clonenova.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.clonenova.common.dto.ResultVO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 시 처리하는 EntryPoint
 * 
 * 인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 호출됩니다.
 * REST-API-SPEC.md의 에러 응답 형식에 맞춰 JSON 응답을 반환합니다.
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        
        log.warn("인증되지 않은 요청: {} {}", request.getMethod(), request.getRequestURI());

        // 응답 상태 코드 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // REST-API-SPEC.md 형식에 맞춘 에러 응답
        ResultVO<Object> result = ResultVO.builder()
                .code(4001)
                .message("인증이 필요합니다.")
                .data(null)
                .build();

        // JSON 응답 작성
        response.getWriter().write(objectMapper.writeValueAsString(result));
        response.getWriter().flush();
    }
}

