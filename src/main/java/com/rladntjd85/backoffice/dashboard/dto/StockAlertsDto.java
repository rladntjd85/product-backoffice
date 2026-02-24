package com.rladntjd85.backoffice.dashboard.dto;

import java.util.List;

/**
 * 재고 알림 데이터를 담는 DTO
 */
public record StockAlertsDto(
        List<ProductAlertDto> lowStock,
        List<ProductAlertDto> soldOut
) {
}