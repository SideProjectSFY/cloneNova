package com.ssafy.clonenova.game.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.clonenova.game.entity.Game;
import com.ssafy.clonenova.game.entity.QGame;
import com.ssafy.clonenova.game.entity.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameCustomRepositoryImpl implements GameCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final QGame qGame = QGame.game;
    private final QProduct qProduct = QProduct.product;

    @Override
    public Game findRandomCodeByLanguageName(String languageName) {
        return jpaQueryFactory
                .selectFrom(qGame)
                .join(qProduct).on(qGame.productId.eq(qProduct.id))
                .where(qProduct.name.eq(languageName))
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
                .limit(1)
                .fetchOne();
    }
}
