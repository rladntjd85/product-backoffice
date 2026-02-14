package com.rladntjd85.backoffice.audit.service;

import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuditService {

    private final AuditLogRepository auditLogRepository;

    public Page<AuditLog> search(String action,
                                 String targetType,
                                 Long targetId,
                                 Long actorId,
                                 int page,
                                 int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        String a = action == null ? "" : action;
        String t = targetType == null ? "" : targetType;

        if (targetId != null && actorId != null) {
            return auditLogRepository
                    .findByActionTypeContainingIgnoreCaseAndTargetTypeContainingIgnoreCaseAndTargetIdAndActorUserId(
                            a, t, targetId, actorId, pageable);
        }

        return auditLogRepository
                .findByActionTypeContainingIgnoreCaseAndTargetTypeContainingIgnoreCase(
                        a, t, pageable);
    }
}
