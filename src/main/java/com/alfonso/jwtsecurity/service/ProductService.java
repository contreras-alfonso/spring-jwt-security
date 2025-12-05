package com.alfonso.jwtsecurity.service;

import com.alfonso.jwtsecurity.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAll();

    Optional<Product> getById(Long id);

    Product save(Product product);

    void deleteById(Long id);
}
