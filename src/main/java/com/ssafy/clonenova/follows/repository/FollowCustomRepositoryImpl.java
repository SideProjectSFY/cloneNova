package com.ssafy.clonenova.follows.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.clonenova.follows.dto.FollowSearchListResponseDTO;
import com.ssafy.clonenova.follows.entity.Follows;
import com.ssafy.clonenova.follows.entity.QFollows;
import com.ssafy.clonenova.follows.entity.QUser;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FollowCustomRepositoryImpl implements FollowCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    // DB 와 매핑된 entity 클래스인 QClass 객체 생성
    private final QFollows qFollows = QFollows.follows;
    private final QFollows targetF = new QFollows("targetF");
    private final QUser qUser = QUser.user;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory2;

    @Override
    public List<FollowSearchListResponseDTO> findFollowerList(String userId, @Nullable String keyword) {
        // follows 와 user 조인
        // keyword 가 존재시 nickname Like 검색 조건 추가
        return jpaQueryFactory
                .select(Projections.constructor(FollowSearchListResponseDTO.class,
                        qFollows.fromUserId.as("userId"),
                        qUser.email,
                        qUser.nickname,
                        Expressions.booleanTemplate(
                                "CASE WHEN {0} IS NOT NULL AND {1} IS NULL" +
                                        "THEN TRUE ELSE FALSE END",
                                targetF.id,
                                targetF.deletedAt
                        ).as("isFollowingBack")
                        ))
                .from(qFollows)
                .join(qUser).on(qFollows.fromUserId.eq(qUser.id))
                .leftJoin(targetF)
                    .on(qFollows.fromUserId.eq(targetF.toUserId)
                    .and(qFollows.toUserId.eq(targetF.fromUserId)))
                .where(qFollows.toUserId.eq(userId)
                        .and(qFollows.deletedAt.isNull())
                        .and(keywordCondition(keyword)))
                .orderBy(qFollows.createdAt.desc())
                .fetch();
    }

    @Override
    public List<FollowSearchListResponseDTO> findFollowingList(String userId, @Nullable String keyword) {
        // follows 와 user 조인
        // keyword 가 존재시 nickname Like 검색 조건 추가
        return jpaQueryFactory
                .select(Projections.constructor(FollowSearchListResponseDTO.class,
                        qFollows.toUserId.as("userId"),
                        qUser.email,
                        qUser.nickname,
                        Expressions.booleanTemplate(
                                "CASE WHEN {0} IS NOT NULL AND {1} IS NULL" +
                                        "THEN TRUE ELSE FALSE END",
                                targetF.id,
                                targetF.deletedAt
                        ).as("isFollowingBack")
                ))
                .from(qFollows)
                .join(qUser).on(qFollows.fromUserId.eq(qUser.id))
                .leftJoin(targetF)
                .on(qFollows.fromUserId.eq(targetF.toUserId)
                        .and(qFollows.toUserId.eq(targetF.fromUserId)))
                .where(qFollows.fromUserId.eq(userId)
                        .and(qFollows.deletedAt.isNull())
                        .and(keywordCondition(keyword)))
                .orderBy(qFollows.createdAt.desc())
                .fetch();
    }

    @Transactional
    @Override
    public Follows addFollow(String fromUserId, String toUserId) {
        return jpaQueryFactory
                .selectFrom(qFollows)
                .where(qFollows.fromUserId.eq(fromUserId)
                        .and(qFollows.toUserId.eq(toUserId))
                        .and(qFollows.deletedAt.isNotNull()))
                .fetchOne();

        // 첫 팔로우
//        if(ObjectUtils.isEmpty(existing)) {
//            Follows newFollow = Follows.builder()
//                    .fromUserId(fromUserId)
//                    .toUserId(toUserId)
//                    .build();
//
//            // 영속성 컨텍스트에 등록 (insert 예약)
//            // 커밋 시점에 실제 insert 쿼리 실행
//            entityManager.persist(newFollow);
//
//            return FollowResponseDTO.builder()
//                    .followId(newFollow.getId())
//                    .fromUserId(newFollow.getFromUserId())
//                    .toUserId(newFollow.getToUserId())
//                    .build();
//
//        }
//        // 과거에 언팔 -> 다시 팔로우 복원
//        else if(existing.getDeletedAt() != null) {
//            // 비어있지않다면 한번 언팔한 사이로 세팅값 변경
//            // -> JPA 필드 변경 감지하여 자동 쿼리 호출됨
//            existing.restore();
//
//            return FollowResponseDTO.builder()
//                    .followId(existing.getId())
//                    .fromUserId(existing.getFromUserId())
//                    .toUserId(existing.getToUserId())
//                    .build();
//        }
//        // 이미 팔로우 중
//        else {
//            return FollowResponseDTO.builder()
//                    .followId(existing.getId())
//                    .fromUserId(existing.getFromUserId())
//                    .toUserId(existing.getToUserId())
//                    .build();
//        }


    }

    @Transactional
    @Override
    public void cancelFollow(String fromUserId, String toUserId) {
        // 언팔 시 deletedAt 컬럼에 현재 시각 세팅
        jpaQueryFactory
            .update(qFollows)
            .set(qFollows.deletedAt, LocalDateTime.now())
            .where(qFollows.fromUserId.eq(fromUserId)
                    .and(qFollows.toUserId.eq(toUserId))
                    .and(qFollows.deletedAt.isNull()))
            .execute();
    }

    // keyword null 체크
    private BooleanExpression keywordCondition(String keyword) {
        return (keyword == null || keyword.isBlank())
                ? null : qUser.nickname.containsIgnoreCase(keyword);
    }
}
