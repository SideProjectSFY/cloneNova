package com.ssafy.clonenova.config;

import com.ssafy.clonenova.users.entity.Avatar;
import com.ssafy.clonenova.users.entity.Role;
import com.ssafy.clonenova.users.repository.AvatarRepository;
import com.ssafy.clonenova.users.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AvatarRepository avatarRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeAvatars();
        initializeRoles();
    }

    private void initializeAvatars() {
        // 기본 아바타가 없으면 생성
        if (!avatarRepository.existsById(1L)) {
            Avatar defaultAvatar = Avatar.builder()
                    .id(1L)
                    .filePath("/default/avatar.png")
                    .build();
            avatarRepository.save(defaultAvatar);
            log.info("기본 아바타 생성 완료: ID=1");
        }
    }

    private void initializeRoles() {
        // ADMIN 역할이 없으면 생성
        if (!roleRepository.existsByAuthority("ROLE_ADMIN")) {
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .authority("ROLE_ADMIN")
                    .description("관리자")
                    .build();
            roleRepository.save(adminRole);
            log.info("ADMIN 역할 생성 완료");
        }

        // USER 역할이 없으면 생성
        if (!roleRepository.existsByAuthority("ROLE_USER")) {
            Role userRole = Role.builder()
                    .name("USER")
                    .authority("ROLE_USER")
                    .description("일반 사용자")
                    .build();
            roleRepository.save(userRole);
            log.info("USER 역할 생성 완료");
        }
    }
}
