package com.rladntjd85.backoffice.audit.dto;

import java.time.LocalDateTime;

public record AdminAuditRow(
        Long id,
        LocalDateTime createdAt,
        String actionType,
        Long actorUserId,
        String actorEmail,
        String targetType,
        Long targetId,
        String ip,
        String userAgent,
        String diffJson
) {}
