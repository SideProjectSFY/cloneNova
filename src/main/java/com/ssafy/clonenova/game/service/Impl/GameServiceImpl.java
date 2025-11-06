package com.ssafy.clonenova.game.service.Impl;

import com.ssafy.clonenova.game.dto.GameRequestDTO;
import com.ssafy.clonenova.game.dto.GameResponseDTO;
import com.ssafy.clonenova.game.entity.Game;
import com.ssafy.clonenova.game.entity.TypingRecord;
import com.ssafy.clonenova.game.repository.GameRepository;
import com.ssafy.clonenova.game.repository.TypingRecordRepository;
import com.ssafy.clonenova.game.service.GameService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Locale;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class GameServiceImpl implements GameService {

    private final TypingRecordRepository typingRecordRepository;
    private final GameRepository gameRepository;

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

    @Override
    public GameResponseDTO.RandomCode getGameRandomCode(String languageName) {
        Game randomCode = gameRepository.findRandomCodeByLanguageName(languageName);
        if(ObjectUtils.isEmpty(randomCode)) {
            throw new IllegalArgumentException("해당 언어의 코드 데이터가 없습니다.");
        }

        return GameResponseDTO.RandomCode
                .builder()
                .gameId(randomCode.getId())
                .code(randomCode.getCode())
                .languageName(languageName.toUpperCase(Locale.ROOT))
                .build();
    }
}
