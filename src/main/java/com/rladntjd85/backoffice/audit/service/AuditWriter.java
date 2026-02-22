package com.rladntjd85.backoffice.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rladntjd85.backoffice.audit.domain.AuditLog;
import com.rladntjd85.backoffice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditWriter {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public void write(Long actorUserId,
                      String actionType,
                      String targetType,
                      Long targetId,
                      String ip,
                      String userAgent,
                      Object diffObjectOrNull) {
        try {
            String diffJson = (diffObjectOrNull == null) ? null : objectMapper.writeValueAsString(diffObjectOrNull);

            auditLogRepository.save(
                    AuditLog.of(actorUserId, actionType, targetType, targetId, ip, userAgent, diffJson)
            );
        } catch (Exception e) {
            // best-effort: 감사 실패로 비즈니스 실패 방지
            log.error("Audit write failed actionType={}, targetType={}, targetId={}",
                    actionType, targetType, targetId, e);
        }
    }
}