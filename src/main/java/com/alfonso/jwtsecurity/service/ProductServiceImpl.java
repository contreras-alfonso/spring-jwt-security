package com.alfonso.jwtsecurity.service;

import com.alfonso.jwtsecurity.entity.Product;
import com.alfonso.jwtsecurity.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
//@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return this.productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> getById(Long id) {
        return this.productRepository.findById(id);
    }

    @Transactional
    public Product save(Product product) {
        return this.productRepository.save(product);
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

}
