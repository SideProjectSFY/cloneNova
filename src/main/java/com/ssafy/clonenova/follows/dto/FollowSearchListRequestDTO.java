package com.ssafy.clonenova.follows.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class FollowSearchListRequestDTO {

    // TODO : JWT 도입 후 토큰 정보에서 로그인 사용자 ID 뽑아올 예정
    private String userId;  // 로그인한 사용자 Id
    private String type;    // follower or following
    private String keyword; // 검색 시 사용 - 타겟 nickname

}
