package com.ssafy.clonenova.follows.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    private String id;

    @Column(name = "avatar_id", nullable = false)
    private String avatarId;

    @Column(name = "email", columnDefinition = "VARCHAR(255)", nullable = false)
    private String email;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String password;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String nickname;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public User(String avatarId, LocalDateTime createdAt, LocalDateTime deletedAt,
                String email, boolean emailVerified, String id, String name, String nickname,
                String password, LocalDateTime updatedAt) {
        this.id = id;
        this.avatarId = avatarId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
