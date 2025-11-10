package com.ssafy.clonenova.follows.controller;

import com.ssafy.clonenova.common.ScrollResponseDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "팔로우 & 팔로잉", description = "팔로우 & 팔로잉 기능 API")
@RestController
@RequestMapping("/api/v1/follows")
@Slf4j
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우 & 팔로잉 리스트 조회 API", description = "로그인한 사용자의 팔로우 & 팔로잉 리스트 조회(+닉네임 기반 검색)")
    @Parameters({
            @Parameter(name = "userId", description = "로그인한 사용자 id(pk) -> 추후 JWT 로 얻을 예정!", required = true),
            @Parameter(name = "type", description = "타입", example = "follower / following", required = true),
            @Parameter(name = "keyword", description = "닉네임 기반 검색 키워드", example = "wangwang"),
            @Parameter(name = "lastId", description = "마지막으로 받은 followId", example = "1"),
            @Parameter(name = "size", description = "가져올 데이터 개수", example = "10"),
    })
    @GetMapping
    public ResponseEntity<ScrollResponseDTO<FollowSearchListResponseDTO>> getFollowList(@ModelAttribute FollowSearchListRequestDTO requestDTO) throws Exception {
        ScrollResponseDTO<FollowSearchListResponseDTO> result = followService.getFollowList(requestDTO);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "팔로우 추가 API", description = "로그인한 사용자가 다른 사용자를 팔로우 하는 기능")
    @Parameters({
            @Parameter(name = "fromUserId", description = "로그인한 사용자 id(pk) -> 추후 JWT 로 얻을 예정!", required = true),
            @Parameter(name = "toUserId", description = "팔로우할 타겟 사용자 id(pk)", required = true),
    })
    @PostMapping
    public ResponseEntity<FollowResponseDTO> follow(@RequestBody FollowRequestDTO requestDTO) throws Exception {

        FollowResponseDTO result = followService.follow(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @Operation(summary = "팔로우 취소 API", description = "로그인한 사용자가 다른 사용자의 팔로우를 취소하는 기능")
    @Parameters({
            @Parameter(name = "fromUserId", description = "로그인한 사용자 id(pk) -> 추후 JWT 로 얻을 예정!", required = true),
            @Parameter(name = "toUserId", description = "팔로우할 타겟 사용자 id(pk)", required = true),
    })
    @PutMapping
    public ResponseEntity<Void> unfollow(@RequestBody FollowRequestDTO requestDTO) {
        followService.unfollow(requestDTO);

        return ResponseEntity.noContent().build();
    }
}
