package com.ssafy.clonenova.game.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.clonenova.game.entity.TypingRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class GameRequestDTO {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Result {
        private String userId;   // TODO : 테스트용 임시 변수
        private Long productId;
        private Double typingSpeed;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        private LocalTime recordedAt;

        // dto -> entity 로 바꾸는 변환 메서드 (jpa 의 경우 파라미터를 entity 만받음)
        public TypingRecord toEntity() {
            return TypingRecord.builder()
                    .userId(userId)
                    .productId(productId)
                    .typingSpeed(typingSpeed)
                    .recordedAt(recordedAt)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }
}
