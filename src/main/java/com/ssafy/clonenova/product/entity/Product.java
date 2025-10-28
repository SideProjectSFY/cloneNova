package com.ssafy.clonenova.product.entity;

import com.ssafy.clonenova.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
    @Column(name = "id", nullable = false)
    private Long id; 

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "des", columnDefinition = "TEXT")
    private String des;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "active", nullable = false)
    private int active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
