package com.ssafy.clonenova.follows.repository;

import com.ssafy.clonenova.follows.entity.Follows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follows, Long>, FollowCustomRepository {


}
