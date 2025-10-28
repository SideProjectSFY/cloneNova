package com.ssafy.clonenova.product.service;

import com.ssafy.clonenova.product.dto.ProductResponse;
import com.ssafy.clonenova.product.entity.Product;
import com.ssafy.clonenova.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse.ListItem> getActiveProducts() {
        List<Product> entities = productRepository
        		.findProductList();
        return entities.stream()
        		.map(ProductResponse::from)
        		.toList();
    }

}
