package com.knu.ynortman.sharedrepository;

import org.springframework.data.repository.CrudRepository;

import com.knu.ynortman.entity.Product;

public interface ProductRepository extends CrudRepository<Product, Long>{

}
