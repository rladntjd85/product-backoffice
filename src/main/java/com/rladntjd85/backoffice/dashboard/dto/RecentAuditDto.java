package com.rladntjd85.backoffice.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 최근 감사로그 항목을 담는 DTO
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record RecentAuditDto(
        Long id,
        String createdAt,
        String actionType,
        String targetType,
        Long targetId,
        Long actorUserId,
        String ip
) {
}