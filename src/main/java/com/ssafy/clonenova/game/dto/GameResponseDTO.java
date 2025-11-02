package com.ssafy.clonenova.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameResponseDTO {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Result {
        private static Long typingRecordId;
        private static Double typingSpeed;
        private static LocalDateTime recordedAt;

        private static Long productId;
    }
}
