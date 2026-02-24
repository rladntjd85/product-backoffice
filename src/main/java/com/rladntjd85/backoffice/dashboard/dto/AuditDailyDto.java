package com.rladntjd85.backoffice.dashboard.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditDailyDto {
    private String date;
    private long count;
}