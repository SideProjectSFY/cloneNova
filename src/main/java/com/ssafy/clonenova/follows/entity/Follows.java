package com.ssafy.clonenova.follows.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follows {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_user_id", columnDefinition = "VARCHAR(36)")
    private String fromUserId;

    @Column(name = "to_user_id", columnDefinition = "VARCHAR(36)")
    private String toUserId;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Follows(String fromUserId, String toUserId, LocalDateTime createdAt,LocalDateTime deletedAt) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public void restore() {
        this.deletedAt = null;
        this.createdAt = LocalDateTime.now();
    }
}
