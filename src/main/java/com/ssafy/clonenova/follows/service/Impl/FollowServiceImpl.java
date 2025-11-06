package com.ssafy.clonenova.follows.service.Impl;

import com.ssafy.clonenova.exception.CustomException;
import com.ssafy.clonenova.common.ScrollResponseDTO;
import com.ssafy.clonenova.follows.dto.FollowRequestDTO;
import com.ssafy.clonenova.follows.dto.FollowResponseDTO;
import com.ssafy.clonenova.follows.dto.FollowSearchListRequestDTO;
import com.ssafy.clonenova.follows.dto.FollowSearchListResponseDTO;
import com.ssafy.clonenova.follows.entity.Follows;
import com.ssafy.clonenova.follows.repository.FollowRepository;
import com.ssafy.clonenova.follows.service.FollowService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true) // 불필요한 flush/더티체킹이 사라져서 조회 성능이 개선됨
    @Override
    public ScrollResponseDTO<FollowSearchListResponseDTO> getFollowList(FollowSearchListRequestDTO requestDTO) {


        int size = (requestDTO.getSize() == null || requestDTO.getSize() <= 0)
                ? 10 : requestDTO.getSize();

        // TODO : currentUser 구현체 통해서 로그인한 사용자 id(pk) 가져올 예정
        String userId = requestDTO.getUserId();
        String keyword = requestDTO.getKeyword();
        String type = requestDTO.getType();

        List<FollowSearchListResponseDTO> resultList;

        if("follower".equalsIgnoreCase(type)) {
            resultList =  followRepository.findFollowerList(userId, keyword, requestDTO.getLastId(), size);
        } else if("following".equalsIgnoreCase(type)){
            resultList =  followRepository.findFollowingList(userId, keyword, requestDTO.getLastId(), size);
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, "잘못된 타입 값입니다. (follower/following만 허용)");
        }

        boolean hasNext = resultList.size() == size;
        Long nextCursor = hasNext ? resultList.get(resultList.size() - 1).getId() : null;

        return new ScrollResponseDTO<>(resultList, nextCursor, hasNext);

    }

    @Override
    public FollowResponseDTO follow(FollowRequestDTO requestDTO) throws Exception {
        // TODO : currentUser 구현체 통해서 로그인한 사용자 id(pk) 가져올 예정
        String fromUserId = requestDTO.getFromUserId();
        // TODO : toUserId null 체크 필요, 진짜 존재하는 유저인지 확인필요
        String toUserId = requestDTO.getToUserId();

        Follows existing = followRepository.findIsFollowCheck(fromUserId, toUserId);

        // 첫 팔로우
        if(ObjectUtils.isEmpty(existing)) {
            Follows newFollow = Follows.builder()
                    .fromUserId(fromUserId)
                    .toUserId(toUserId)
                    .createdAt(LocalDateTime.now())
                    .build();

            // 영속성 컨텍스트에 등록 (insert 예약)
            // 커밋 시점에 실제 insert 쿼리 실행
            entityManager.persist(newFollow);

            return FollowResponseDTO.builder()
                    .followId(newFollow.getId())
                    .fromUserId(newFollow.getFromUserId())
                    .toUserId(newFollow.getToUserId())
                    .build();

        }
        // 과거에 언팔 -> 다시 팔로우 복원
        else if(existing.getDeletedAt() != null) {
            // 비어있지않다면 한번 언팔한 사이로 세팅값 변경
            // -> JPA 필드 변경 감지하여 자동 쿼리 호출됨
            existing.restore();

            return FollowResponseDTO.builder()
                    .followId(existing.getId())
                    .fromUserId(existing.getFromUserId())
                    .toUserId(existing.getToUserId())
                    .build();
        }
        // 이미 팔로우 중 (or errorCode 로 반환하거나)
        else {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 팔로우 중인 상태입니다.");
        }
    }

    @Override
    public void unfollow(FollowRequestDTO requestDTO) {
        // TODO : currentUser 구현체 통해서 로그인한 사용자 id(pk) 가져올 예정
        String fromUserId = requestDTO.getFromUserId();
        // TODO : toUserId null 체크 필요, 진짜 존재하는 유저인지 확인 필요
        String toUserId = requestDTO.getToUserId();

        followRepository.cancelFollow(fromUserId, toUserId);
    }
}
