package com.ssafy.clonenova.payment.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PortOneClient {

    @Value("${portone.apiKey}")    
    private String apiKey;
    
    @Value("${portone.secretKey}") 
    private String secretKey;

    private static final String BASE = "https://api.iamport.kr";
    private final RestTemplate rt = new RestTemplate();

    /** v1: Access Token 발급 */
    private String getAccessToken() {
        String url = BASE + "/users/getToken";
        Map<String, String> body = Map.of("imp_key", apiKey, "imp_secret", secretKey);
        HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
        Map resp = rt.postForObject(url, new HttpEntity<>(body, h), Map.class);
        Map data = (Map) resp.get("response");
        return (String) data.get("access_token");
    }

    /** 결제 단건 조회 */
    public Map<String, Object> getPaymentInfo(String impUid) {
        String token = getAccessToken();
        String url = BASE + "/payments/" + impUid;

        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", token);

        ResponseEntity<Map> res = rt.exchange(url, HttpMethod.GET, new HttpEntity<>(h), Map.class);
        return (Map<String, Object>) res.getBody().get("response");
    }
}
