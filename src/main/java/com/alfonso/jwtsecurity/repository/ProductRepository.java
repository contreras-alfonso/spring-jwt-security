package com.alfonso.jwtsecurity.repository;

import com.alfonso.jwtsecurity.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
