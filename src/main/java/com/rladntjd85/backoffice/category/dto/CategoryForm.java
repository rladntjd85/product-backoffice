package com.rladntjd85.backoffice.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryForm(
        Long id,
        @NotBlank(message = "카테고리명은 필수입니다.")
        String name
) {}
