package com.ssafy.clonenova.game.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_best_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBestRecord {

    @Id
    private Long productId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "best_speed")
    private Double bestSpeed;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
