package com.ssafy.clonenova.game.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameRequestDTO {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Result {
        private static String userId;   // TODO : 테스트용 임시 변수
        private static Long productId;
        private static Double typingSpeed;
        private static LocalDateTime recordedAt;
    }
}
