package com.ssafy.clonenova.game.repository;

import com.ssafy.clonenova.game.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 프로그래밍 언어명을 검색하여 일치하는 id를 조회합니다.
     * @param name 언어명
     * @return Long
     * */
    @Query("select p.id from Product p where p.name = :name")
    Long findIdByName(@Param("name") String name);

}
