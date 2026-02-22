package com.rladntjd85.backoffice.category.api;

import com.rladntjd85.backoffice.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api/categories") // 대시보드와 일관성 유지
@RequiredArgsConstructor
public class AdminCategoryApiController {

    private final CategoryRepository categoryRepository;

    // Fetch가 호출할 경로: /admin/api/categories/{parentId}/children
    @GetMapping("/{parentId}/children")
    public List<CategoryResponse> getChildren(@PathVariable Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();
    }

    // JSON 응답용 DTO (엔티티 직접 반환 방지)
    public record CategoryResponse(Long id, String name) {}
}
