package com.ssafy.clonenova.follows.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowResponseDTO {

    private Long followId;
    private String fromUserId;
    private String toUserId;

    @Builder
    public FollowResponseDTO(Long followId, String fromUserId, String toUserId) {
        this.followId = followId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

}
