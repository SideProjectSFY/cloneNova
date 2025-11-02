package com.ssafy.clonenova.users.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 사용자 기본 정보 엔티티
 * 
 * ERD 기반 필드:
 * - id: UUID v4 (PK)
 * - email: 로그인 ID 겸용, UNIQUE
 * - password: BCrypt 해시
 * - name: 실명
 * - nickname: 2-20자, UNIQUE
 * - provider: 계정 타입 (local, google, kakao)
 * - provider_id: OAuth 제공자 고유 ID
 * - email_verified: 이메일 인증 여부
 * - Soft Delete: deleted_at 필드로 계정 비활성화 관리
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
public class User {

    /**
     * 사용자 ID (UUID v4)
     * Primary Key
     */
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    /**
     * 아바타 ID (FK to avatar)
     * 기본값: 1 (기본 아바타)
     */
    @Column(name = "avatar_id", nullable = false)
    private Long avatarId;

    /**
     * 이메일 주소 (로그인 ID 겸용)
     * UNIQUE 제약조건
     */
    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    /**
     * 비밀번호 (BCrypt 해시)
     * 모든 계정은 비밀번호 필수 (OAuth 연동 후에도 필요)
     */
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    /**
     * 실명
     */
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    /**
     * 닉네임 (2-20자)
     * UNIQUE 제약조건
     */
    @Column(name = "nickname", length = 100, nullable = false, unique = true)
    private String nickname;

    /**
     * 계정 타입 (local, google, kakao)
     * 기본값: 'local'
     */
    @Column(name = "provider", length = 50, nullable = false)
    private String provider = "local";

    /**
     * OAuth 제공자의 고유 ID
     * Local 계정은 NULL
     */
    @Column(name = "provider_id", length = 255)
    private String providerId;

    /**
     * 이메일 인증 여부
     * 기본값: false
     */
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    /**
     * 생성 시간
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 삭제 시간 (Soft Delete)
     * NULL: 활성 계정
     * NOT NULL: 비활성 계정
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 아바타 엔티티와의 관계 (Many-to-One)
     * FetchType.LAZY: 지연 로딩
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id", insertable = false, updatable = false)
    private Avatar avatar;

    /**
     * 엔티티 생성 시 실행되는 메서드
     * UUID 생성 및 생성 시간 설정
     */
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.avatarId == null) {
            this.avatarId = 1L; // 기본 아바타 ID
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

    /**
     * 계정이 활성화 상태인지 확인
     * 
     * @return deleted_at이 NULL이면 true (활성), 아니면 false (비활성)
     */
    public boolean isActive() {
        return this.deletedAt == null;
    }

    /**
     * 계정 비활성화 (Soft Delete)
     */
    public void deactivate() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 계정 활성화 (Soft Delete 복구)
     */
    public void activate() {
        this.deletedAt = null;
    }

    /**
     * 사용자 생성 팩토리 메서드 (회원가입용)
     * 
     * @param email 이메일 주소
     * @param password 비밀번호 (암호화된 상태)
     * @param name 실명
     * @param nickname 닉네임
     * @return 생성된 User 엔티티
     */
    public static User create(String email, String password, String name, String nickname) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setNickname(nickname);
        user.setProvider("local");
        user.setEmailVerified(false);
        // id, createdAt, avatarId는 @PrePersist에서 자동 설정
        return user;
    }
}
