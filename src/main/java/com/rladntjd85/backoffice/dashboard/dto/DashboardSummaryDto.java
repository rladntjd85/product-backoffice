package com.rladntjd85.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DashboardSummaryDto {
    private long totalProducts;
    private long activeProducts;
    private long hiddenProducts;
    private long soldOutProducts;
    private long deletedProducts;
    private long todayEvents;
    private long todayLoginFail;
}
