package com.ssafy.clonenova.follows.repository;

import com.ssafy.clonenova.follows.dto.FollowSearchListResponseDTO;
import com.ssafy.clonenova.follows.entity.Follows;
import jakarta.annotation.Nullable;

import java.util.List;

public interface FollowCustomRepository {

    /**
     * 사용자의 팔로우 목록 조회
     *
     * @param userId 사용자 ID
     * @param keyword 타겟 닉네임 검색 (null 허용)
     * @return 사용자를 팔로우한 유저 리스트
     * */
    List<FollowSearchListResponseDTO> findFollowerList(String userId, @Nullable String keyword, @Nullable Long lastId, int size);

    /**
     * 사용자의 팔로잉 목록 조회
     *
     * @param userId 사용자 ID
     * @param keyword 타겟 닉네임 검색 (null 허용)
     * @return 사용자가 팔로잉한 유저 리스트
     * */
    List<FollowSearchListResponseDTO> findFollowingList(String userId, @Nullable String keyword, @Nullable Long lastId, int size);

    /**
     * 팔로우 존재여부 확인
     *
     * @param fromUserId 사용자 ID
     * @param toUserId   타겟 사용자 ID 검색
     * @return 팔로우 객체 반환
     */
    Follows findIsFollowCheck(String fromUserId, String toUserId);

    /**
     * 팔로우 취소
     *
     * @param fromUserId 사용자 ID (=pk)
     * @param toUserId 타겟 사용자 ID (=pk) 검색
     * */
    void cancelFollow(String fromUserId, String toUserId);
}
