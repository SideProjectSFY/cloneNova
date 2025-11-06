package com.ssafy.clonenova.game.controller;

import com.ssafy.clonenova.game.dto.GameRequestDTO;
import com.ssafy.clonenova.game.dto.GameResponseDTO;
import com.ssafy.clonenova.game.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "게임 기능", description = "게임 결과 저장 및 랭킹, 통계 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/games")
public class GameController {

    private final GameService gameService;

    @Operation(summary = "게임 결과 저장 API", description = "사용자가 게임한 결과를 서버로 전송하여 저장하는 기능")
    @Parameters({
            @Parameter(name = "userId", description = "로그인한 사용자 id(pk) -> 추후 받아올 예정!", required = true),
            @Parameter(name = "productId", description = "상품Id(언어 id)", required = true),
            @Parameter(name = "typingSpeed", description = "타이핑속도", example = "278.5"),
            @Parameter(name = "recordedAt", description = "기록시간", example = "00:27:13"),
    })
    @PostMapping
    public ResponseEntity<GameResponseDTO.Result> saveTypingRecordResult(@RequestBody GameRequestDTO.Result requestResultDTO) {
        GameResponseDTO.Result result = gameService.saveTypingRecordResult(requestResultDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @Operation(summary = "게임 코드 랜덤 조회 API", description = "프로그래밍 언어 목록을 클릭하면 해당 언어로 코드를 랜덤으로 조회하는 기능")
    @Parameter(name = "language", description = "언어", example = "JAVA", required = true)
    @GetMapping("/random")
    public ResponseEntity<GameResponseDTO.RandomCode> getGameRandomCode(@RequestParam("language") String languageName) {
        GameResponseDTO.RandomCode result = gameService.getGameRandomCode(languageName);
        return ResponseEntity.ok(result);
    }
}
