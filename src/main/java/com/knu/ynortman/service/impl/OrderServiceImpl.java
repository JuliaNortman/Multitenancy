package com.knu.ynortman.service.impl;

import com.knu.ynortman.dto.OrderDto;
import com.knu.ynortman.entity.Order;
import com.knu.ynortman.repository.OrderRepository;
import com.knu.ynortman.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepo;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public List<OrderDto> getOrders() {
        return StreamSupport.stream(orderRepo.findAll().spliterator(), false)
                .map(OrderDto::fromOrder)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrder(long id) {
        return orderRepo.findById(id)
                .map(OrderDto::fromOrder)
                .orElseThrow();
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = orderRepo.save(OrderDto.toOrderWithoutId(orderDto));
        return OrderDto.fromOrder(order);
    }
}
