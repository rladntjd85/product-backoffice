package com.rladntjd85.backoffice.dashboard.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionTopDto {
    private String actionType;
    private long count;
}