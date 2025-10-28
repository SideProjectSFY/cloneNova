package com.ssafy.clonenova.product.repository;

import com.ssafy.clonenova.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
           SELECT p
             FROM Product p
            WHERE p.active = 1
              AND p.deletedAt IS NULL
         ORDER BY p.createdAt DESC
           """)
    List<Product> findProductList();
}
