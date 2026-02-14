package com.rladntjd85.backoffice.audit.repository;

import com.rladntjd85.backoffice.audit.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByActionTypeContainingIgnoreCaseAndTargetTypeContainingIgnoreCase(
            String actionType,
            String targetType,
            Pageable pageable
    );

    Page<AuditLog> findByActionTypeContainingIgnoreCaseAndTargetTypeContainingIgnoreCaseAndTargetIdAndActorUserId(
            String actionType,
            String targetType,
            Long targetId,
            Long actorUserId,
            Pageable pageable
    );
}
