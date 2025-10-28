package com.ssafy.clonenova.users.repository;

import com.ssafy.clonenova.users.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByAuthority(String authority);

    boolean existsByAuthority(String authority);
}