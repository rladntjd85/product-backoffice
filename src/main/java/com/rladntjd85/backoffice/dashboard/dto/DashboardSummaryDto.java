package com.rladntjd85.backoffice.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    private long totalProducts;
    private long activeProducts;
    private long hiddenProducts;
    private long soldOutProducts;
    private long deletedProducts;
    private long todayEvents;
    private long todayLoginFail;
}
