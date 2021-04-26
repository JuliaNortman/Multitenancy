package com.knu.ynortman.sharedrepository;

import com.knu.ynortman.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
