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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "팔로우 & 팔로잉", description = "팔로우 & 팔로잉 기능 API")
@RestController
@RequestMapping("/follows")
@Slf4j
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우 & 팔로잉 리스트 조회 API", description = "로그인한 사용자의 팔로우 & 팔로잉 리스트 조회(+닉네임 기반 검색)")
    @Parameters({
            @Parameter(name = "userId", description = "로그인한 사용자 id(pk) -> 추후 JWT 로 얻을 예정!", required = true),
            @Parameter(name = "type", description = "타입", example = "follower / following", required = true),
            @Parameter(name = "keyword", description = "닉네임 기반 검색 키워드", example = "wangwang"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "4002", description = "타입 불일치"),
            @ApiResponse(responseCode = "4100", description = "타입 값이 null"),
    })
    @GetMapping
    public ResponseEntity<ResultVO<List<FollowSearchListResponseDTO>>> getFollowList(@ModelAttribute FollowSearchListRequestDTO requestDTO) throws Exception {
        List<FollowSearchListResponseDTO> result = followService.getFollowList(requestDTO);
        return ResponseEntity.ok(ResultVO.success(result));
    }


    @Operation(summary = "팔로우 추가 API", description = "로그인한 사용자가 다른 사용자를 팔로우 하는 기능")
    @Parameters({
            @Parameter(name = "fromUserId", description = "로그인한 사용자 id(pk) -> 추후 JWT 로 얻을 예정!", required = true),
            @Parameter(name = "toUserId", description = "팔로우할 타겟 사용자 id(pk)", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "4101", description = "이미 팔로우중인 상태"),
    })
    @PostMapping
    public ResponseEntity<ResultVO<FollowResponseDTO>> follow(@RequestBody FollowRequestDTO requestDTO) throws Exception {

        FollowResponseDTO result = followService.follow(requestDTO);
        return ResponseEntity.ok(ResultVO.success(result));
    }


    @Operation(summary = "팔로우 취소 API", description = "로그인한 사용자가 다른 사용자의 팔로우를 취소하는 기능")
    @Parameters({
            @Parameter(name = "fromUserId", description = "로그인한 사용자 id(pk) -> 추후 JWT 로 얻을 예정!", required = true),
            @Parameter(name = "toUserId", description = "팔로우할 타겟 사용자 id(pk)", required = true),
    })
    @PutMapping
    public ResponseEntity<ResultVO<Void>> unfollow(@RequestBody FollowRequestDTO requestDTO) {
        followService.unfollow(requestDTO);

        return ResponseEntity.ok(ResultVO.success(null));
    }
}
