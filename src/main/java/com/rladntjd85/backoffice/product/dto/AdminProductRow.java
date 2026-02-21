package com.rladntjd85.backoffice.product.dto;

import java.time.LocalDateTime;

public record AdminProductRow(
        Long id,
        String name,
        Long categoryId,
        String categoryName,
        int price,
        int stock,
        String status,
        String thumbnailUrl,
        LocalDateTime createdAt
) {
}
