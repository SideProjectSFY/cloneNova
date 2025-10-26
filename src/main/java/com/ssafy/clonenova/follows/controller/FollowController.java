package com.ssafy.clonenova.follows.controller;

import com.ssafy.clonenova.common.ResultVO;
import com.ssafy.clonenova.follows.dto.FollowRequestDTO;
import com.ssafy.clonenova.follows.dto.FollowResponseDTO;
import com.ssafy.clonenova.follows.dto.FollowSearchListRequestDTO;
import com.ssafy.clonenova.follows.dto.FollowSearchListResponseDTO;
import com.ssafy.clonenova.follows.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "팔로우 & 팔로잉", description = "팔로우 & 팔로잉 기능 API")
@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
public class FollowController {

    // TODO :  서비스 로직 작성 후 추가 작성 필요

    private final FollowService followService;

    @Operation(summary = "팔로우 & 팔로잉 리스트 조회 API", description = "로그인한 사용자의 팔로우 & 팔로잉 리스트 조회(+닉네임 기반 검색)")
    @Parameters({
            @Parameter(name = "userId", description = "로그인한 사용자 id(pk) -> 추후 JWT 로 얻을 예정!", required = true),
            @Parameter(name = "type", description = "타입", example = "follower / following", required = true),
            @Parameter(name = "keyword", description = "닉네임 기반 검색 키워드", example = "wangwang"),
    })
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공"),
//            @ApiResponse(responseCode = "403", description = "인증 불일치"),
//            @ApiResponse(responseCode = "5001", description = "조회된 결과 없음")
//    })
    @GetMapping("/")
    public ResponseEntity<ResultVO<FollowSearchListResponseDTO>> getFollowList(@ModelAttribute FollowSearchListRequestDTO requestDTO) {
        return ResponseEntity.ok(ResultVO.success(null));
    }


    @Operation(summary = "팔로우 추가 API", description = "로그인한 사용자가 다른 사용자를 팔로우 하는 기능")
    @Parameters({
            @Parameter(name = "fromUserId", description = "로그인한 사용자 id(pk) -> 추후 JWT 로 얻을 예정!", required = true),
            @Parameter(name = "toUserId", description = "팔로우할 타겟 사용자 id(pk)", required = true),
    })
    @PostMapping("/")
    public ResponseEntity<ResultVO<FollowResponseDTO>> follow(@ModelAttribute FollowRequestDTO requestDTO) {
        return ResponseEntity.ok(ResultVO.success(null));
    }


    @Operation(summary = "팔로우 취소 API", description = "로그인한 사용자가 다른 사용자의 팔로우를 취소하는 기능")
    @Parameters({
            @Parameter(name = "fromUserId", description = "로그인한 사용자 id(pk) -> 추후 JWT 로 얻을 예정!", required = true),
            @Parameter(name = "toUserId", description = "팔로우할 타겟 사용자 id(pk)", required = true),
    })
    @PutMapping("/")
    public ResponseEntity<ResultVO<Void>> unfollow(@ModelAttribute FollowSearchListRequestDTO requestDTO) {
        return ResponseEntity.ok(ResultVO.success(null));
    }
}
