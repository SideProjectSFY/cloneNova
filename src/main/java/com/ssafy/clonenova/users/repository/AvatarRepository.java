package com.ssafy.clonenova.users.repository;

import com.ssafy.clonenova.users.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
}
