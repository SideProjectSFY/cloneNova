package com.ssafy.clonenova.game.service.Impl;

import com.ssafy.clonenova.game.dto.GameRequestDTO;
import com.ssafy.clonenova.game.dto.GameResponseDTO;
import com.ssafy.clonenova.game.entity.TypingRecord;
import com.ssafy.clonenova.game.repository.TypingRecordRepository;
import com.ssafy.clonenova.game.service.GameService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class GameServiceImpl implements GameService {

    private final TypingRecordRepository typingRecordRepository;

    @Override
    public GameResponseDTO.Result saveTypingRecordResult(GameRequestDTO.Result requestResultDTO) {

        // dto -> entity 변환 (jpa 의 파라미터는 entity 를 받아서)
        TypingRecord recordEntity = requestResultDTO.toEntity();
        // jpa 기본 메서드
        TypingRecord savedResult = typingRecordRepository.save(recordEntity);

        return GameResponseDTO.Result.builder()
                .typingRecordId(savedResult.getId())
                .productId(savedResult.getProductId())
                .typingSpeed(savedResult.getTypingSpeed())
                .recordedAt(savedResult.getRecordedAt())
                .build();
    }
}
