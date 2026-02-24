package com.rladntjd85.backoffice.dashboard.dto;

/**
 * 최근 감사로그 항목을 담는 DTO
 */
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