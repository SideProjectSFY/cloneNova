package com.ssafy.clonenova.game.repository;

import com.ssafy.clonenova.game.entity.Game;

public interface GameCustomRepository {

    /**
     *  게임 코드 랜덤 조회
     *
     * @param languageName 언어명 (=상품명)
     * @return Game 객체
     * */
    Game findRandomCodeByLanguageName(String languageName);
}
