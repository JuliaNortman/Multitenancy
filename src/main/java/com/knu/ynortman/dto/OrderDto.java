package com.knu.ynortman.dto;

import com.knu.ynortman.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private long id;
    private UserDto user;
    private ProductDto product;
    private String address;

    public static OrderDto fromOrder(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .user(UserDto.fromUser(order.getUser()))
                .product(ProductDto.fromProduct(order.getProduct()))
                .address(order.getAddress())
                .build();
    }

    public static Order toOrder(OrderDto orderDto) {
        return Order.builder()
                .id(orderDto.getId())
                .user(UserDto.toUser(orderDto.getUser()))
                .product(ProductDto.toProduct(orderDto.getProduct()))
                .address(orderDto.getAddress())
                .build();
    }

    public static Order toOrderWithoutId(OrderDto orderDto) {
        return Order.builder()
                .user(UserDto.toUser(orderDto.getUser()))
                .product(ProductDto.toProduct(orderDto.getProduct()))
                .address(orderDto.getAddress())
                .build();
    }
}