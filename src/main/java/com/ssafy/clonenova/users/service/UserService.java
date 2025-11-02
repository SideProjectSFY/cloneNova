package com.ssafy.clonenova.users.service;

import com.ssafy.clonenova.auth.dto.request.RegisterRequestDTO;
import com.ssafy.clonenova.users.entity.User;
import com.ssafy.clonenova.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관리 서비스
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 생성 (회원가입)
     * 
     * @param request 회원가입 요청 DTO
     * @return 생성된 User 엔티티
     */
    @Transactional
    public User create(RegisterRequestDTO request) {
        // DTO를 Entity로 변환 (비밀번호 암호화 포함)
        User user = request.toEntity(passwordEncoder);
        
        // id, createdAt, avatarId는 @PrePersist에서 자동 설정
        
        return userRepository.save(user);
    }
}
