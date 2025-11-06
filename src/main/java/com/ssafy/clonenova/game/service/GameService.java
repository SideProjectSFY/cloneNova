package com.ssafy.clonenova.game.service;

import com.ssafy.clonenova.game.dto.GameRequestDTO;
import com.ssafy.clonenova.game.dto.GameResponseDTO;

public interface GameService {

    // 게임 결과 저장
    GameResponseDTO.Result saveTypingRecordResult(GameRequestDTO.Result requestResultDTO);

    // 게임 코드 랜덤 조회
    GameResponseDTO.RandomCode getGameRandomCode(String languageName);

}
