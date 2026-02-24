package com.rladntjd85.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

/**
 * 재고 알림 데이터를 담는 DTO
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record StockAlertsDto(
        List<ProductAlertDto> lowStock,
        List<ProductAlertDto> soldOut
) {
}