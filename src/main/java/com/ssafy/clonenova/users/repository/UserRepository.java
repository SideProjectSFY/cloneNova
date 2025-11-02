package com.ssafy.clonenova.users.repository;

import com.ssafy.clonenova.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 사용자 데이터 액세스 레이어
 */
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * 이메일로 사용자 조회 (Soft Delete 고려)
     * 
     * @param email 이메일 주소
     * @return Optional<User>
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findByEmail(@Param("email") String email);
    
    /**
     * 닉네임으로 사용자 조회 (Soft Delete 고려)
     * 
     * @param nickname 닉네임
     * @return Optional<User>
     */
    @Query("SELECT u FROM User u WHERE u.nickname = :nickname AND u.deletedAt IS NULL")
    Optional<User> findByNickname(@Param("nickname") String nickname);
    
    /**
     * 이메일 존재 여부 확인 (Soft Delete 고려)
     * 
     * @param email 이메일 주소
     * @return 존재하면 true
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);
    
    /**
     * 닉네임 존재 여부 확인 (Soft Delete 고려)
     * 
     * @param nickname 닉네임
     * @return 존재하면 true
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.nickname = :nickname AND u.deletedAt IS NULL")
    boolean existsByNickname(@Param("nickname") String nickname);
}
