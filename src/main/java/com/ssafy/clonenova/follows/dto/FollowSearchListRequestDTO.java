package com.ssafy.clonenova.follows.dto;

import lombok.*;


@Getter
@AllArgsConstructor
public class FollowSearchListRequestDTO {

    // TODO : currentUser 구현체 통해서 로그인한 사용자 id(pk) 가져올 예정
    private String userId;  // 로그인한 사용자 Id
    private String type;    // follower or following
    private String keyword; // 검색 시 사용 - 타겟 nickname

    // Scroll
    private Long lastId;
    private Integer size;

}
