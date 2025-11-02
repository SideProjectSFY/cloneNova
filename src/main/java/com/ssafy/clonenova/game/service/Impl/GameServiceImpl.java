package com.ssafy.clonenova.game.service.Impl;

import com.ssafy.clonenova.game.dto.GameRequestDTO;
import com.ssafy.clonenova.game.dto.GameResponseDTO;
import com.ssafy.clonenova.game.repository.TypingRecordRepository;
import com.ssafy.clonenova.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {

    private final TypingRecordRepository typingRecordRepository;

    @Override
    public GameResponseDTO.Result saveTypingRecordResult(GameRequestDTO.Result requestResultDTO) {

        return null;
    }
}
