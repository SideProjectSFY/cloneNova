package com.ssafy.clonenova.game.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "typing_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TypingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "typing_speed", columnDefinition = "DOUBLE DEFAULT '0.0'", nullable = false)
    private Double typingSpeed;

    @Column(name = "recorded_at", columnDefinition = "TIME DEFAULT '00:00:00'")
    private LocalDateTime recordedAt;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Builder
    public TypingRecord(Long productId, String userId, double typingSpeed, LocalDateTime recordedAt, LocalDateTime createdAt) {
        this.productId = productId;
        this.userId = userId;
        this.typingSpeed = typingSpeed;
        this.recordedAt = recordedAt;
        this.createdAt = createdAt;
    }
}
