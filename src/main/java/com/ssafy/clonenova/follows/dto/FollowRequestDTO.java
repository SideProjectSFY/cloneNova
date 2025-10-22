package com.ssafy.clonenova.follows.dto;

import com.ssafy.clonenova.follows.entity.Follows;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowRequestDTO {

    // TODO : JWT 도입 후 토큰 정보에서 로그인 사용자 ID 뽑아올 예정
    private String fromUserId;
    private String toUserId;

    @Builder
    public FollowRequestDTO(String fromUserId, String toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    // dto -> entity 로 바꾸는 변환 메서드
    public Follows toEntity() {
        return Follows.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .build();
    }
}
