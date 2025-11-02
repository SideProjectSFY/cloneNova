package com.ssafy.clonenova.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * reCAPTCHA 설정 Properties
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "recaptcha")
public class RecaptchaProperties {
    
    /**
     * reCAPTCHA Secret Key
     */
    private Secret secret = new Secret();
    
    @Getter
    @Setter
    public static class Secret {
        /**
         * Google reCAPTCHA Secret Key
         */
        private String key;
    }
}
