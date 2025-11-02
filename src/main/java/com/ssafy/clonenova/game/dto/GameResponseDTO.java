package com.ssafy.clonenova.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class GameResponseDTO {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Result {
        private Long typingRecordId;
        private Double typingSpeed;
        private LocalTime recordedAt;

        private Long productId;
    }
}
