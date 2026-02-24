package com.rladntjd85.backoffice.dashboard.dto;

import java.util.List;

public record StockAlertsDto(
        List<ProductAlertDto> lowStock,
        List<ProductAlertDto> soldOut
) {}