package com.ssafy.clonenova.follows.service;

import com.ssafy.clonenova.common.ScrollResponseDTO;
import com.ssafy.clonenova.follows.dto.FollowRequestDTO;
import com.ssafy.clonenova.follows.dto.FollowResponseDTO;
import com.ssafy.clonenova.follows.dto.FollowSearchListRequestDTO;
import com.ssafy.clonenova.follows.dto.FollowSearchListResponseDTO;

public interface FollowService {

    ScrollResponseDTO<FollowSearchListResponseDTO> getFollowList(FollowSearchListRequestDTO requestDTO) throws Exception;

    FollowResponseDTO follow(FollowRequestDTO requestDTO) throws Exception;

    void unfollow(FollowRequestDTO requestDTO);

}
