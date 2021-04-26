package com.knu.ynortman.service;

import com.knu.ynortman.dto.OrderDto;

import java.util.List;

public interface OrderService {
    List<OrderDto> getOrders();
    OrderDto getOrder(long id);
    OrderDto createOrder(OrderDto order);
}
