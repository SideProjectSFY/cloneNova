package com.ssafy.clonenova.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "portone")
public record PortOneProperties(
        String apiKey,
        String secretKey
) {}
