package com.rladntjd85.backoffice.audit.repository;

import com.rladntjd85.backoffice.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
