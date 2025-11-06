package com.ssafy.clonenova.follows.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowSearchListResponseDTO {

    private Long id;
    private String userId;
    private String email;
    private String nickname;
    private boolean isFollowingBack;   // 맞팔여부

    @Builder
    public FollowSearchListResponseDTO(Long id,String userId, String email, String nickname, boolean isFollowingBack) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.isFollowingBack = isFollowingBack;
    }
}
