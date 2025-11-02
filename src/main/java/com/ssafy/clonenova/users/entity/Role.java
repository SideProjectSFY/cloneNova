package com.ssafy.clonenova.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 역할 엔티티
 * 
 * ERD 기반 필드:
 * - id: BIGINT AUTO_INCREMENT (PK)
 * - name: 역할 이름 (ADMIN, USER)
 * - authority: 권한 코드 (ROLE_ADMIN, ROLE_USER) - UNIQUE
 * - description: 설명
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "role",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_authority", columnNames = "authority")
       })
public class Role {

    /**
     * 역할 ID
     * Primary Key, AUTO_INCREMENT
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 역할 이름 (ADMIN, USER)
     */
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    /**
     * 권한 코드 (ROLE_ADMIN, ROLE_USER)
     * UNIQUE 제약조건
     */
    @Column(name = "authority", length = 100, nullable = false, unique = true)
    private String authority;

    /**
     * 설명
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 생성 시간
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 엔티티 생성 시 실행되는 메서드
     * 생성 시간 자동 설정
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 엔티티 수정 시 실행되는 메서드
     * 수정 시간 자동 업데이트
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
