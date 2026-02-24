package com.rladntjd85.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 알림 대상 개별 상품 정보
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record ProductAlertDto(
        Long id,
        String name,
        Integer stock,
        String status
) {
}