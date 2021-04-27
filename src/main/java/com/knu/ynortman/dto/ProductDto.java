package com.knu.ynortman.dto;

import com.knu.ynortman.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private long id;
    private String name;
    private float price;

    public static ProductDto fromProduct(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .price(product.getPrice())
                .name(product.getName())
                .build();
    }

    public static Product toProduct(ProductDto productDto) {
        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .build();
    }

    public static Product toProductWithoutId(ProductDto productDto) {
        return Product.builder()
                .name(productDto.getName())
                .price(productDto.getPrice())
                .build();
    }
}
