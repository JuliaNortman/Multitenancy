package com.knu.ynortman.service;

import com.knu.ynortman.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProducts();
    ProductDto getProduct(long id);
    ProductDto createProduct(ProductDto product);
}
