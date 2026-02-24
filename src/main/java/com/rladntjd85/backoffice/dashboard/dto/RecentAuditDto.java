package com.rladntjd85.backoffice.dashboard.dto;

public record RecentAuditDto(
        Long id,
        String createdAt,
        String actionType,
        String targetType,
        Long targetId,
        Long actorUserId,
        String ip
) {}
