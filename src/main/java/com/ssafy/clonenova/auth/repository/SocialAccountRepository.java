package com.ssafy.clonenova.auth.repository;

import com.ssafy.clonenova.auth.entity.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    // 사용자 ID로 소셜 계정 목록 조회 (삭제되지 않은 것만)
    List<SocialAccount> findByUserIdAndDeletedAtIsNull(String userId);

    // Provider와 ProviderAccountId로 소셜 계정 조회
    Optional<SocialAccount> findByProviderAndProviderAccountIdAndDeletedAtIsNull(String provider, String providerAccountId);

    // 사용자의 특정 Provider 소셜 계정 조회
    Optional<SocialAccount> findByUserIdAndProviderAndDeletedAtIsNull(String userId, String provider);
}
