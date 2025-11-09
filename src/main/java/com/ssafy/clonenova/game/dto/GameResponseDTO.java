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

        private String language;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class RandomCode {
        private Long gameId;
        private String code;
        private String language;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Ranking {
       private String userId;
       private String nickName;
       private Double typingSpeed;          // 점수
       private int rank;
    }
}
