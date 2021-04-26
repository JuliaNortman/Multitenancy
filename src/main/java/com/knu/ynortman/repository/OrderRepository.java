package com.knu.ynortman.repository;

import com.knu.ynortman.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
