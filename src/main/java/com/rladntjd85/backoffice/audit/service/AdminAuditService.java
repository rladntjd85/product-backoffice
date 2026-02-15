package com.rladntjd85.backoffice.audit.service;

import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.dto.AdminAuditRow;
import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuditService {

    private final AuditLogRepository auditLogRepository;

    public Page<AdminAuditRow> search(String action, String targetType, Long targetId, String actorEmail, String targetUserEmail, LocalDateTime fromDt, LocalDateTime toDtExclusive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return auditLogRepository.searchRows(action, targetType, targetId, actorEmail, targetUserEmail, fromDt, toDtExclusive, pageable);
    }
}
