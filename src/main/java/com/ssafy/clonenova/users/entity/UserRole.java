package com.ssafy.clonenova.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 사용자-역할 매핑 엔티티 (Many-to-Many)
 * 
 * ERD 기반 필드:
 * - user_id: VARCHAR(36) (PK, FK to User)
 * - role_id: BIGINT (PK, FK to Role)
 * - 복합 키 사용
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_role")
@IdClass(UserRoleId.class)
public class UserRole {

    /**
     * 사용자 ID (복합 키 일부)
     */
    @Id
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    /**
     * 역할 ID (복합 키 일부)
     */
    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * 생성 시간 (권한 부여 시점)
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * User 엔티티와의 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * Role 엔티티와의 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

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
