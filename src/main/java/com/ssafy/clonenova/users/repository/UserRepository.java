package com.ssafy.clonenova.users.repository;

import com.ssafy.clonenova.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 데이터 액세스 레이어
 */
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * 이메일로 사용자 조회
     * 
     * @param email 이메일 주소
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 닉네임으로 사용자 조회
     * 
     * @param nickname 닉네임
     * @return Optional<User>
     */
    Optional<User> findByNickname(String nickname);
    
    /**
     * 이메일 존재 여부 확인
     * 
     * @param email 이메일 주소
     * @return 존재하면 true
     */
    boolean existsByEmail(String email);
    
    /**
     * 닉네임 존재 여부 확인
     * 
     * @param nickname 닉네임
     * @return 존재하면 true
     */
    boolean existsByNickname(String nickname);
}
