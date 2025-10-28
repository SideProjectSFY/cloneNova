package com.ssafy.clonenova.users.repository;

import com.ssafy.clonenova.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // 이메일로 사용자 조회 (삭제되지 않은 사용자만)
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findByEmailAndDeletedAtIsNull(@Param("email") String email);

    // 이메일 중복 확인 (삭제되지 않은 사용자만)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    boolean existsByEmailAndDeletedAtIsNull(@Param("email") String email);

    // 닉네임 중복 확인 (삭제되지 않은 사용자만)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.nickname = :nickname AND u.deletedAt IS NULL")
    boolean existsByNicknameAndDeletedAtIsNull(@Param("nickname") String nickname);

    // Provider와 ProviderId로 사용자 조회
    Optional<User> findByProviderAndProviderIdAndDeletedAtIsNull(String provider, String providerId);

    // 이메일로 사용자 존재 여부 확인 (삭제된 사용자 포함)
    boolean existsByEmail(String email);
}