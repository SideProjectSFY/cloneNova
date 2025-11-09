package com.ssafy.clonenova.game.service.Impl;

import com.ssafy.clonenova.game.dto.GameRequestDTO;
import com.ssafy.clonenova.game.dto.GameResponseDTO;
import com.ssafy.clonenova.game.entity.Game;
import com.ssafy.clonenova.game.entity.TypingRecord;
import com.ssafy.clonenova.game.repository.GameRepository;
import com.ssafy.clonenova.game.repository.ProductRepository;
import com.ssafy.clonenova.game.repository.TypingRecordRepository;
import com.ssafy.clonenova.game.service.GameService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private final TypingRecordRepository typingRecordRepository;
    private final GameRepository gameRepository;
    private final ProductRepository productRepository;

    private final StringRedisTemplate redisTemplate;
    private static final String RANKING_KEY_FMT = "ranking:%s";

    /**
     * 게임 결과 서버에 저장 및 Redis 랭킹 갱신
     * */
    @Transactional
    @Override
    public GameResponseDTO.Result saveTypingRecordResult(GameRequestDTO.Result requestResultDTO) {

        // 파라미터에서 받은 이름을 통해 id 가져오기
        String languageName = requestResultDTO.getLanguage();
        // TODO : 파라미터 빈 값 체크하는 customException 적용 예정

        Long languageId = productRepository.findIdByName(languageName);
        // TODO : 언어테이블에 없는 언어명이 들어왔을 경우 customException 적용 예정


        // dto -> entity 변환 (jpa 의 파라미터는 entity 만 받음)
        TypingRecord recordEntity = requestResultDTO.toEntity(languageId);
        // jpa 기본 메서드
        TypingRecord savedResult = typingRecordRepository.save(recordEntity);
        // TODO : 저장 값이 비어있는 경우에도 customException 적용 예정

        // Redis SortedSet 업데이트 (= 점수 갱신)
        String key = RANKING_KEY_FMT.formatted(languageName.toLowerCase());
        Double score = requestResultDTO.getTypingSpeed();
        // TODO : currentUser 받아올 예정
        String userId = requestResultDTO.getUserId();

        // redis 에서 사용자의 기존 점수를 가져온다. (Redis 명령어 : ZSCORE)
        Double oldScore = redisTemplate.opsForZSet().score(key, userId);

        if (oldScore == null || score > oldScore) {
            // 처음 게임하는 사용자거나, 기존 점수보다 높은 점수일 경우 갱신!
            // (Redis 명령어 : ZADD)
            redisTemplate.opsForZSet().add(key, userId, score);
        }

        return GameResponseDTO.Result.builder()
                .typingRecordId(savedResult.getId())
//                .languageId(savedResult.getProductId())
                .language(languageName)
                .typingSpeed(savedResult.getTypingSpeed())
                .recordedAt(savedResult.getRecordedAt())
                .build();
    }

    /**
    * 게임 코드 랜덤 조회
     *  @param languageName 언어명 (ex. JAVA)
    * */
    @Override
    public GameResponseDTO.RandomCode getGameRandomCode(String languageName) {
        Game randomCode = gameRepository.findRandomCodeByLanguageName(languageName);
        if(ObjectUtils.isEmpty(randomCode)) {
            throw new IllegalArgumentException("해당 언어의 코드 데이터가 없습니다.");
        }

        return GameResponseDTO.RandomCode
                .builder()
                .gameId(randomCode.getId())
                .code(randomCode.getCode())
                .language(languageName.toUpperCase(Locale.ROOT))
                .build();
    }

    /**
     * 상위 N명 랭킹 조회
     */
    public List<GameResponseDTO.Ranking> getTopRanking(String language, int limit) {
        String key = RANKING_KEY_FMT.formatted(language.toLowerCase());

        // 점수 높은 순(=내림차순) 으로 조회 (Redis 명령어 : ZREVRANGE WITHSCORES)
        Set<ZSetOperations.TypedTuple<String>> top =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);

        if (top == null || top.isEmpty()) return List.of();

        // Redis 에서 userId 목록 추출
        List<String> userIdList = new ArrayList<>();
        for(ZSetOperations.TypedTuple<String> tuple : top) {
            String userId = tuple.getValue();
            if(userId != null) userIdList.add(userId);
        }


        // TODO : DB에서 닉네임 매핑 조회 필요
//        Map<String, String> nicknameMap = userRepository.findNicknamesByUserIds(userIds)
//                .stream()
//                .collect(Collectors.toMap(UserNicknameDTO::getUserId, UserNicknameDTO::getNickname));

        // TODO : 매핑하여 DTO 생성
        AtomicInteger rank = new AtomicInteger(1);
        return top.stream()
                .map(tuple -> new GameResponseDTO.Ranking(
                        tuple.getValue(),  // userId
                        "닉네임_" + tuple.getValue(), // (DB나 캐시에서 가져와도 됨)
//                        nicknameMap.getOrDefault(tuple.getValue(), "Unknown"),
                        tuple.getScore(),
                        rank.getAndIncrement()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 특정 유저의 랭킹 조회
     */
    public Optional<GameResponseDTO.Ranking> getUserRanking(String language, String userId) {
        String key = RANKING_KEY_FMT.formatted(language.toLowerCase());
        Double score = redisTemplate.opsForZSet().score(key, userId);
        Long rank = redisTemplate.opsForZSet().reverseRank(key, userId);

        if (score == null || rank == null) return Optional.empty();

        return Optional.of(new GameResponseDTO.Ranking(userId, "닉네임_" + userId, score, rank.intValue() + 1));
    }

}
