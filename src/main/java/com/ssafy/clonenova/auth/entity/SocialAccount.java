package com.ssafy.clonenova.auth.entity;

import com.ssafy.clonenova.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 소셜 계정 연동 정보 엔티티
 * 
 * ERD 기반 필드:
 * - id: BIGINT AUTO_INCREMENT (PK)
 * - user_id: VARCHAR(36) (FK to User)
 * - provider: 제공자 (google, kakao)
 * - provider_account_id: OAuth 제공자의 계정 ID
 * - provider_email: OAuth 제공자 이메일
 * - provider_name: OAuth 제공자 이름
 * - Soft Delete: deleted_at 필드로 연동 해제 관리
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "social_accounts",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_provider_account", columnNames = {"provider", "provider_account_id"})
       },
       indexes = {
           @Index(name = "fk_user", columnList = "user_id")
       })
public class SocialAccount {

    /**
     * 소셜 계정 ID
     * Primary Key, AUTO_INCREMENT
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 사용자 ID (FK to User)
     */
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    /**
     * 제공자 (google, kakao)
     */
    @Column(name = "provider", length = 100, nullable = false)
    private String provider;

    /**
     * OAuth 제공자의 계정 ID
     */
    @Column(name = "provider_account_id", length = 255, nullable = false)
    private String providerAccountId;

    /**
     * OAuth 제공자 이메일
     */
    @Column(name = "provider_email", length = 255)
    private String providerEmail;

    /**
     * OAuth 제공자 이름
     */
    @Column(name = "provider_name", length = 100)
    private String providerName;

    /**
     * 연동일시
     */
    @Column(name = "linked_at", nullable = false, updatable = false)
    private LocalDateTime linkedAt;

    /**
     * 생성 시간
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 삭제 시간 (Soft Delete - 연동 해제 시점)
     * NULL: 활성
     * NOT NULL: 연동 해제됨
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * User 엔티티와의 관계 (Many-to-One)
     * FetchType.LAZY: 지연 로딩
     * CASCADE DELETE: 사용자 삭제 시 함께 삭제
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * 엔티티 생성 시 실행되는 메서드
     * 연동 시간 및 생성 시간 자동 설정
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.linkedAt == null) {
            this.linkedAt = now;
        }
        if (this.createdAt == null) {
            this.createdAt = now;
        }
    }
}
