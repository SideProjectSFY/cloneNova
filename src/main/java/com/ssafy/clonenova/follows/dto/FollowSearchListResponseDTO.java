package com.ssafy.clonenova.follows.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowSearchListResponseDTO {

    private String userId;
    private String email;
    private String nickname;
    private boolean isFollowingBack;   // 맞팔여부
//    private String avatarFilePath;     // 프로필이미지경로

    @Builder
    public FollowSearchListResponseDTO(String userId, String email, String nickname, boolean isFollowingBack
//            , String avatarFilePath
    ) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.isFollowingBack = isFollowingBack;
//        this.avatarFilePath = avatarFilePath;
    }
}
