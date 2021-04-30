package com.knu.ynortman.service.impl;

import com.knu.ynortman.dto.ProductDto;
import com.knu.ynortman.entity.Product;
import com.knu.ynortman.service.ProductService;
import com.knu.ynortman.sharedrepository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProductServiceImpl implements ProductService {

	/*@Override
	public List<ProductDto> getProducts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductDto getProduct(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductDto createProduct(ProductDto product) {
		// TODO Auto-generated method stub
		return null;
	}*/
    private final ProductRepository productRepo;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public List<ProductDto> getProducts() {
        return StreamSupport.stream(productRepo.findAll().spliterator(), false)
                .map(ProductDto::fromProduct)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProduct(long id) {
        return productRepo.findById(id)
                .map(ProductDto::fromProduct)
                .orElseThrow();
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productRepo.save(ProductDto.toProduct(productDto));
        return ProductDto.fromProduct(product);
    }
}
