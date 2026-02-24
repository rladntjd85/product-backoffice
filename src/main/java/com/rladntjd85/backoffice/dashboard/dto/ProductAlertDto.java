package com.rladntjd85.backoffice.dashboard.dto;

/**
 * 알림 대상 개별 상품 정보
 */
public record ProductAlertDto(
        Long id,
        String name,
        Integer stock,
        String status
) {
}