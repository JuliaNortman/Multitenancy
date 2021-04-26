package com.knu.ynortman.controller;

import com.knu.ynortman.dto.ProductDto;
import com.knu.ynortman.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts() {
        List<ProductDto> productValues = productService.getProducts();
        return new ResponseEntity<List<ProductDto>>(productValues, HttpStatus.OK);
    }

    @GetMapping(value = "/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("productId") long productId) {
        try {
            ProductDto branch = productService.getProduct(productId);
            return new ResponseEntity<>(branch, HttpStatus.OK);
        } catch (Exception e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productValue) {
        ProductDto product = productService.createProduct(productValue);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }
}
