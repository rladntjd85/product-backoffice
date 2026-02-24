package com.rladntjd85.backoffice.dashboard.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryDto {
    private long totalProducts;
    private long activeProducts;
    private long hiddenProducts;
    private long soldOutProducts;
    private long deletedProducts;
    private long todayEvents;
    private long todayLoginFail;
}
