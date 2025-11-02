package com.ssafy.clonenova.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 아바타 이미지 엔티티
 * 
 * ERD 기반 필드:
 * - id: BIGINT AUTO_INCREMENT (PK)
 * - file_path: 이미지 파일 경로
 * - Soft Delete: deleted_at 필드로 삭제 관리
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "avatar")
public class Avatar {

    /**
     * 아바타 ID
     * Primary Key, AUTO_INCREMENT
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 이미지 파일 경로
     */
    @Column(name = "file_path", length = 500, nullable = false)
    private String filePath;

    /**
     * 생성 시간
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 삭제 시간 (Soft Delete)
     * NULL: 활성
     * NOT NULL: 삭제됨
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 엔티티 생성 시 실행되는 메서드
     * 생성 시간 자동 설정
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
