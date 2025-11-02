package com.ssafy.clonenova.game.repository;

import com.ssafy.clonenova.game.entity.TypingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypingRecordRepository extends JpaRepository<TypingRecord, Long> {}
