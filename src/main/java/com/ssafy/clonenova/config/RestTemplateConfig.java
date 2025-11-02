package com.ssafy.clonenova.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate 설정
 * reCAPTCHA 검증 등 외부 API 호출용
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());  // 연결 타임아웃 5초
        factory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());     // 읽기 타임아웃 5초
        
        return new RestTemplate(factory);
    }
}
