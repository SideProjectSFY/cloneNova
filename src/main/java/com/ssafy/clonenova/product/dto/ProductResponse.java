package com.ssafy.clonenova.product.dto;

import lombok.*;
import java.time.LocalDateTime;

import com.ssafy.clonenova.product.entity.Product;

public class ProductResponse {

    /** [SF01] 상품 목록 리스트 */
	@Getter
	@Builder
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ListItem {
        private Long id;
        private String name;
        private Integer price;
        private Integer active;
    }
	
	private ProductResponse() {}
	
	// product Entity 를 dto로 변환
	public static ListItem from(Product p) {
	    return ListItem.builder()
	            .id(p.getId())
	            .name(p.getName())
	            .price(p.getPrice())
	            .active(p.getActive())
	            .build();
	}
}
