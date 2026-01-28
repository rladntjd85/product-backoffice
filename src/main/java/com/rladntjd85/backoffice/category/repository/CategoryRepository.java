package com.rladntjd85.backoffice.category.repository;

import com.rladntjd85.backoffice.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
