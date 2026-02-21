package com.rladntjd85.backoffice.category.service;

import com.rladntjd85.backoffice.category.domain.Category;
import com.rladntjd85.backoffice.category.dto.CategoryForm;
import com.rladntjd85.backoffice.category.repository.CategoryRepository;
import com.rladntjd85.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private static final String LOCK_MSG = "상품을 이동하거나 삭제 후에 수정 및 변경가능합니다.";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Category> search(String q, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return categoryRepository.search(q, status, pageable);
    }

    @Transactional(readOnly = true)
    public long totalCount(String q) {
        return categoryRepository.countAllByQ(q);
    }

    @Transactional(readOnly = true)
    public long activeCount(String q) {
        return categoryRepository.countActiveByQ(q);
    }

    @Transactional(readOnly = true)
    public long inactiveCount(String q) {
        return categoryRepository.countInactiveByQ(q);
    }

    @Transactional(readOnly = true)
    public Category get(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
    }

    @Transactional
    public Long create(CategoryForm form) {
        if (categoryRepository.existsByNameIgnoreCase(form.name())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리명입니다.");
        }

        Category c = Category.builder()
                .name(form.name().trim())
                .enabled(true)
                .build();

        return categoryRepository.save(c).getId();
    }

    @Transactional
    public void rename(Long id, String newName) {
        Category c = get(id);

        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalArgumentException(LOCK_MSG);
        }

        String name = newName.trim();
        if (!c.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리명입니다.");
        }

        c.rename(name);
    }

    @Transactional
    public void disable(Long id) {
        Category c = get(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalArgumentException(LOCK_MSG);
        }
        c.disable();
    }

    @Transactional
    public void enable(Long id) {
        Category c = get(id);
        c.enable();
    }

    @Transactional
    public void delete(Long id) {
        // v1: 물리 삭제 (단, 상품 있으면 금지)
        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalArgumentException(LOCK_MSG);
        }
        categoryRepository.deleteById(id);
    }
}
