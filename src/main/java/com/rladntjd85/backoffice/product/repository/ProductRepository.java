package com.rladntjd85.backoffice.product.repository;

import com.rladntjd85.backoffice.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}