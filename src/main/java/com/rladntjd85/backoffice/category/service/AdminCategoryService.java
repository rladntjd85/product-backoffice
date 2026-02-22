package com.rladntjd85.backoffice.category.service;

import com.rladntjd85.backoffice.audit.annotation.AuditLoggable;
import com.rladntjd85.backoffice.audit.annotation.AuditTargetId;
import com.rladntjd85.backoffice.category.domain.Category;
import com.rladntjd85.backoffice.category.dto.CategoryForm;
import com.rladntjd85.backoffice.category.repository.CategoryRepository;
import com.rladntjd85.backoffice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private static final String LOCK_MSG = "상품을 이동하거나 삭제 후에 수정 및 변경가능합니다.";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Category> search(String q, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
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

    @Transactional(readOnly = true)
    public List<Category> getParentList() {
        return categoryRepository.findByParentIsNullAndEnabledTrueOrderByNameAsc();
    }

    /**
     * 카테고리 생성: 반환되는 ID를 기반으로 생성 직후 스냅샷을 기록합니다.
     */
    @Transactional
    @AuditLoggable(action = "CATEGORY_CREATED", targetType = "CATEGORY", entityClass = Category.class)
    public Long create(CategoryForm form) {
        Category parent = null;
        if (form.parentId() != null) {
            parent = get(form.parentId());
        }

        if (categoryRepository.existsByParentAndNameIgnoreCase(parent, form.name())) {
            throw new IllegalArgumentException("이미 해당 위치에 존재하는 카테고리명입니다.");
        }

        Category c = Category.builder()
                .name(form.name().trim())
                .parent(parent)
                .enabled(true)
                .build();

        return categoryRepository.save(c).getId();
    }

    /**
     * 카테고리 수정: 부모 카테고리 ID 변경이나 이름 변경 내역을 AOP가 자동으로 추적합니다.
     */
    @Transactional
    @AuditLoggable(action = "CATEGORY_UPDATED", targetType = "CATEGORY", entityClass = Category.class)
    public void update(@AuditTargetId Long id, CategoryForm form) {
        Category c = get(id);

        Long newParentId = form.parentId();
        Category currentParent = c.getParent();
        Long currentParentId = (currentParent != null) ? currentParent.getId() : null;

        if (isParentChanged(currentParentId, newParentId)) {
            if (id.equals(newParentId)) {
                throw new IllegalArgumentException("자기 자신을 상위 카테고리로 지정할 수 없습니다.");
            }
            if (!c.getChildren().isEmpty() && newParentId != null) {
                throw new IllegalArgumentException("하위 카테고리가 존재하는 카테고리는 다른 카테고리의 하위로 이동할 수 없습니다.");
            }

            if (newParentId != null) {
                Category parent = get(newParentId);
                c.changeParent(parent);
            } else {
                c.changeParent(null);
            }
        }

        String newName = form.name().trim();
        if (!c.getName().equalsIgnoreCase(newName)) {
            if (categoryRepository.existsByParentAndNameIgnoreCase(c.getParent(), newName)) {
                throw new IllegalArgumentException("이미 해당 위치에 존재하는 카테고리명입니다.");
            }
            c.rename(newName);
        }
    }

    @Transactional(readOnly = true)
    public List<Category> getParentListExceptMe(Long id) {
        return categoryRepository.findByParentIsNullAndEnabledTrueAndIdNotOrderByNameAsc(id);
    }

    @Transactional
    @AuditLoggable(action = "CATEGORY_DISABLED", targetType = "CATEGORY", entityClass = Category.class)
    public void disable(@AuditTargetId Long id) {
        Category c = get(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalArgumentException(LOCK_MSG);
        }
        c.disable();
    }

    @Transactional
    @AuditLoggable(action = "CATEGORY_ENABLED", targetType = "CATEGORY", entityClass = Category.class)
    public void enable(@AuditTargetId Long id) {
        Category c = get(id);
        c.enable();
    }

    /**
     * 카테고리 삭제: 물리 삭제의 경우 삭제 전 스냅샷만 남깁니다.
     */
    @Transactional
    @AuditLoggable(action = "CATEGORY_DELETED", targetType = "CATEGORY", entityClass = Category.class)
    public void delete(@AuditTargetId Long id) {
        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalArgumentException(LOCK_MSG);
        }
        categoryRepository.deleteById(id);
    }

    private boolean isParentChanged(Long currentId, Long newId) {
        if (currentId == null && newId == null) return false;
        if (currentId == null || newId == null) return true;
        return !currentId.equals(newId);
    }
}