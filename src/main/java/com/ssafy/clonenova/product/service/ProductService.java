package com.ssafy.clonenova.product.service;

import java.util.List;

import com.ssafy.clonenova.product.dto.ProductResponse;

public interface ProductService {

    /** [SF01] 상품 목록 리스트 */
    List<ProductResponse.ListItem> getActiveProducts();
}
