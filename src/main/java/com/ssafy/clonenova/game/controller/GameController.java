package com.ssafy.clonenova.game.controller;

import com.ssafy.clonenova.game.dto.GameRequestDTO;
import com.ssafy.clonenova.game.dto.GameResponseDTO;
import com.ssafy.clonenova.game.service.GameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "게임 기능", description = "게임 관련 기능 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResponseDTO> saveTypinRecordResult(@RequestBody GameRequestDTO.Result requestResultDTO) {
        GameResponseDTO.Result result = gameService.saveTypingRecordResult(requestResultDTO);
        return null;
    }
}
