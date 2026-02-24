package com.rladntjd85.backoffice.dashboard.dto;

public record ProductAlertDto(
        Long id,
        String name,
        Integer stock,
        String status
) {}
